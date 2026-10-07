package com.gov.licence.workflow;
import com.gov.licence.model.*;
import java.sql.*;
import java.util.*;
/** Prepared SQL, owner scoping and conditional updates enforce record access and lifecycle. */
public final class LicenceService {
    private final Database db;
    private static final String SELECT="SELECT r.*, o.full_name AS owner_name, v.full_name AS reviewer_name FROM licence_records r JOIN app_users o ON o.id=r.owner_id LEFT JOIN app_users v ON v.id=r.reviewer_id";
    public LicenceService(Database db){this.db=db;}
    public List<LicenceApplication> list(User actor,String keyword,String status) {
        authenticated(actor);
        String query=keyword==null?"":keyword.trim();
        if(query.length()>100) throw new WorkflowException(400,"Search must be at most 100 characters.");
        if(status!=null && !status.isBlank()) try {ApplicationStatus.valueOf(status);} catch(IllegalArgumentException ex){throw new WorkflowException(400,"Invalid status filter.");}
        String sql=SELECT+" WHERE 1=1"+(actor.getRole()==Role.BUSINESS_OWNER?" AND r.owner_id=?":"")+
            (query.isEmpty()?"":" AND (LOCATE(LOWER(?),LOWER(r.business_name))>0 OR LOCATE(LOWER(?),LOWER(r.registration_number))>0 OR LOCATE(LOWER(?),LOWER(r.status))>0)")+
            (status==null || status.isBlank()?"":" AND r.status=?")+" ORDER BY r.created_at DESC,r.id DESC";
        try(Connection c=db.connect(); PreparedStatement ps=c.prepareStatement(sql)) {
            int i=1; if(actor.getRole()==Role.BUSINESS_OWNER) ps.setLong(i++,actor.getId());
            if(!query.isEmpty()) for(int j=0;j<3;j++) ps.setString(i++,query);
            if(status!=null && !status.isBlank()) ps.setString(i,status);
            List<LicenceApplication> rows=new ArrayList<>(); try(ResultSet rs=ps.executeQuery()){while(rs.next())rows.add(map(rs));} return rows;
        } catch(SQLException ex){throw storage(ex);}
    }
    public Map<String,Long> summary(User actor) {
        authenticated(actor);
        Map<String,Long> counts=new LinkedHashMap<>(); counts.put("TOTAL",0L); counts.put("PENDING",0L);
        for(ApplicationStatus status:ApplicationStatus.values()) counts.put(status.name(),0L);
        try(Connection c=db.connect(); PreparedStatement ps=c.prepareStatement("SELECT status,COUNT(*) AS amount FROM licence_records"+(actor.getRole()==Role.BUSINESS_OWNER?" WHERE owner_id=?":"")+" GROUP BY status")) {
            if(actor.getRole()==Role.BUSINESS_OWNER) ps.setLong(1,actor.getId());
            try(ResultSet rs=ps.executeQuery()){while(rs.next()){String status=rs.getString("status");long n=rs.getLong("amount");counts.put(status,n);counts.put("TOTAL",counts.get("TOTAL")+n);if(status.equals("SUBMITTED")||status.equals("UNDER_REVIEW"))counts.put("PENDING",counts.get("PENDING")+n);}}
            return counts;
        }catch(SQLException ex){throw storage(ex);}
    }
    public LicenceApplication get(User actor,long id) {
        authenticated(actor);
        try(Connection c=db.connect();PreparedStatement ps=c.prepareStatement(SELECT+" WHERE r.id=?")) {
            ps.setLong(1,id); try(ResultSet rs=ps.executeQuery()) {
                if(!rs.next())throw new WorkflowException(404,"Application not found.");
                LicenceApplication row=map(rs);
                if(actor.getRole()==Role.BUSINESS_OWNER && row.getOwnerId()!=actor.getId())throw new WorkflowException(403,"You cannot access another owner's application.");
                return row;
            }
        }catch(SQLException ex){throw storage(ex);}
    }
    public long create(User actor,Map<String,String> fields) {
        role(actor,Role.BUSINESS_OWNER);ApplicationInput in=ApplicationInput.parse(fields);
        try(Connection c=db.connect();PreparedStatement ps=c.prepareStatement("INSERT INTO licence_records(business_name,registration_number,trade_category,annual_turnover,expiry_date,contact_email,contact_phone,owner_id,status) VALUES(?,?,?,?,?,?,?,?,'SUBMITTED')",Statement.RETURN_GENERATED_KEYS)) {
            bind(ps,in);ps.setLong(8,actor.getId());ps.executeUpdate();try(ResultSet rs=ps.getGeneratedKeys()){rs.next();return rs.getLong(1);}
        }catch(SQLException ex){throw storage(ex);}
    }
    public void update(User actor,long id,Map<String,String> fields) {
        role(actor,Role.BUSINESS_OWNER);get(actor,id);ApplicationInput in=ApplicationInput.parse(fields);
        try(Connection c=db.connect();PreparedStatement ps=c.prepareStatement("UPDATE licence_records SET business_name=?,registration_number=?,trade_category=?,annual_turnover=?,expiry_date=?,contact_email=?,contact_phone=?,updated_at=CURRENT_TIMESTAMP WHERE id=? AND owner_id=? AND status='SUBMITTED'")) {
            bind(ps,in);ps.setLong(8,id);ps.setLong(9,actor.getId()); changed(ps.executeUpdate());
        }catch(SQLException ex){throw storage(ex);}
    }
    public void delete(User actor,long id) {
        role(actor,Role.BUSINESS_OWNER);get(actor,id);
        try(Connection c=db.connect();PreparedStatement ps=c.prepareStatement("DELETE FROM licence_records WHERE id=? AND owner_id=? AND status='SUBMITTED'")) {
            ps.setLong(1,id);ps.setLong(2,actor.getId());changed(ps.executeUpdate());
        }catch(SQLException ex){throw storage(ex);}
    }
    public void startReview(User actor,long id) {
        role(actor,Role.LICENSING_OFFICER);get(actor,id);
        try(Connection c=db.connect();PreparedStatement ps=c.prepareStatement("UPDATE licence_records SET status='UNDER_REVIEW',updated_at=CURRENT_TIMESTAMP WHERE id=? AND status='SUBMITTED'")) {
            ps.setLong(1,id);changed(ps.executeUpdate());
        }catch(SQLException ex){throw storage(ex);}
    }
    public void decide(User actor,long id,String decision,String remarks) {
        role(actor,Role.LICENSING_OFFICER);get(actor,id);
        if(!"APPROVED".equals(decision) && !"REJECTED".equals(decision))throw new WorkflowException(400,"Choose APPROVED or REJECTED.");
        String note=ApplicationInput.remarks(remarks);
        try(Connection c=db.connect()) {
            c.setAutoCommit(false);
            try {
                try(PreparedStatement ps=c.prepareStatement("UPDATE licence_records SET status=?,reviewer_id=?,remarks=?,decided_at=CURRENT_TIMESTAMP,updated_at=CURRENT_TIMESTAMP WHERE id=? AND status IN ('SUBMITTED','UNDER_REVIEW')")) {
                    ps.setString(1,decision);ps.setLong(2,actor.getId());ps.setString(3,note);ps.setLong(4,id);changed(ps.executeUpdate());
                }
                try(PreparedStatement ps=c.prepareStatement("INSERT INTO audit_remarks(record_id,officer_id,action_taken,remarks_text) VALUES(?,?,?,?)")) {
                    ps.setLong(1,id);ps.setLong(2,actor.getId());ps.setString(3,decision);ps.setString(4,note);ps.executeUpdate();
                }
                c.commit();
            }catch(SQLException | RuntimeException ex){c.rollback();throw ex;}
        }catch(SQLException ex){throw storage(ex);}
    }
    private static void authenticated(User actor){if(actor==null)throw new WorkflowException(403,"Sign in first.");}
    private static void role(User actor,Role role){authenticated(actor);if(actor.getRole()!=role)throw new WorkflowException(403,"This action is not allowed for your role.");}
    private static void changed(int n){if(n!=1)throw new WorkflowException(409,"Application changed or is no longer editable. Refresh and try again.");}
    private static WorkflowException storage(SQLException ex){
        if("23505".equals(ex.getSQLState()))return new WorkflowException(409,"This registration number already has an application.");
        throw new IllegalStateException("Licence database operation failed",ex);
    }
    private static void bind(PreparedStatement ps,ApplicationInput in)throws SQLException {
        ps.setString(1,in.businessName());ps.setString(2,in.registrationNumber());ps.setString(3,in.tradeCategory());ps.setBigDecimal(4,in.annualTurnover());
        ps.setObject(5,in.expiryDate());ps.setString(6,in.contactEmail());ps.setString(7,in.contactPhone());
    }
    private static LicenceApplication map(ResultSet rs)throws SQLException {
        Timestamp decision=rs.getTimestamp("decided_at");
        return new LicenceApplication(rs.getLong("id"),rs.getLong("owner_id"),rs.getString("owner_name"),rs.getString("registration_number"),rs.getString("business_name"),rs.getString("trade_category"),rs.getBigDecimal("annual_turnover"),rs.getDate("expiry_date").toLocalDate(),rs.getString("contact_email"),rs.getString("contact_phone"),ApplicationStatus.valueOf(rs.getString("status")),rs.getTimestamp("created_at").toLocalDateTime(),rs.getTimestamp("updated_at").toLocalDateTime(),rs.getString("remarks"),rs.getString("reviewer_name"),decision==null?null:decision.toLocalDateTime());
    }
}
