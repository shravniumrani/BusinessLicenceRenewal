package com.gov.licence.workflow;
import com.gov.licence.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
class LicenceServiceTest {
    private final User owner=new User(1,"owner1","Aarav",Role.BUSINESS_OWNER);
    private final User other=new User(2,"owner2","Meera",Role.BUSINESS_OWNER);
    private final User officer=new User(3,"officer","Officer",Role.LICENSING_OFFICER);
    private Database db;
    private LicenceService service;
    @BeforeEach void setup(){db=new Database("jdbc:h2:mem:"+UUID.randomUUID()+";DB_CLOSE_DELAY=-1");service=new LicenceService(db);}
    @AfterEach void close()throws SQLException {try(Connection c=db.connect();Statement s=c.createStatement()){s.execute("SHUTDOWN");}}
    static Map<String,String> valid(String registration) {
        return new HashMap<>(Map.of("businessName","Apex Retailers", "registrationNumber",registration,
            "tradeCategory","General Retail","annualTurnover","4500000.50","expiryDate","2026-12-31",
            "contactEmail","contact@apex.example","contactPhone","9876543210"));
    }
    private void fails(int status,Runnable action){assertEquals(status,assertThrows(WorkflowException.class,action::run).getStatus());}
    @Test void createAndSearchAreScopedToOwner(){
        long id=service.create(owner,valid("REG-001"));service.create(other,valid("REG-002"));
        assertEquals(ApplicationStatus.SUBMITTED,service.get(owner,id).getStatus());
        assertEquals(1,service.list(owner,"apex","").size());assertEquals(2,service.list(officer,"","").size());
        assertTrue(service.list(owner,"002","").isEmpty());assertTrue(service.list(owner,"%' OR 1=1 --","").isEmpty());
        assertEquals(1L,service.summary(owner).get("TOTAL"));assertEquals(2L,service.summary(officer).get("PENDING"));
    }
    @Test void ownerCannotReadEditOrDeleteAnotherOwnersRecord(){
        long id=service.create(owner,valid("REG-001"));
        fails(403,()->service.get(other,id));fails(403,()->service.update(other,id,valid("REG-999")));fails(403,()->service.delete(other,id));
        assertEquals("REG-001",service.get(owner,id).getRegistrationNumber());
    }
    @Test void submittedRecordsCanBeEditedAndDeleted(){
        long id=service.create(owner,valid("REG-001"));Map<String,String> edit=valid("REG-001");edit.put("businessName","Updated Shop");
        service.update(owner,id,edit);assertEquals("Updated Shop",service.get(owner,id).getBusinessName());
        service.delete(owner,id);fails(404,()->service.get(owner,id));assertEquals(0L,service.summary(owner).get("TOTAL"));
    }
    @Test void reviewLocksOwnerMutationsAndApprovalIsAudited()throws SQLException{
        long id=service.create(owner,valid("REG-001"));service.startReview(officer,id);
        fails(409,()->service.update(owner,id,valid("REG-001")));fails(409,()->service.delete(owner,id));
        service.decide(officer,id,"APPROVED","All details verified.");LicenceApplication row=service.get(owner,id);
        assertEquals(ApplicationStatus.APPROVED,row.getStatus());assertEquals("All details verified.",row.getRemarks());
        assertNotNull(row.getDecidedAt());assertEquals("Licensing Officer",row.getReviewerName());
        assertEquals(1L,service.summary(officer).get("APPROVED"));assertEquals(0L,service.summary(officer).get("PENDING"));
        fails(409,()->service.decide(officer,id,"REJECTED","Changed my mind"));
        try(Connection c=db.connect();Statement s=c.createStatement();ResultSet rs=s.executeQuery("SELECT COUNT(*) FROM audit_remarks")){rs.next();assertEquals(1,rs.getInt(1));}
    }
    @Test void rejectionRequiresRemarksAndValidDecision(){
        long id=service.create(owner,valid("REG-001"));fails(400,()->service.decide(officer,id,"REJECTED","  "));
        fails(400,()->service.decide(officer,id,"SUBMITTED","Note"));assertEquals(ApplicationStatus.SUBMITTED,service.get(owner,id).getStatus());
        service.decide(officer,id,"REJECTED","Incorrect registration details.");assertEquals(1,service.list(owner,"","REJECTED").size());
        fails(409,()->service.delete(owner,id));
    }
    @Test void rolesAreEnforcedForEveryMutation(){
        long id=service.create(owner,valid("REG-001"));
        fails(403,()->service.create(officer,valid("REG-002")));fails(403,()->service.update(officer,id,valid("REG-001")));
        fails(403,()->service.delete(officer,id));fails(403,()->service.startReview(owner,id));fails(403,()->service.decide(owner,id,"APPROVED","Ok"));
        fails(403,()->service.list(null,"",""));
    }
    @Test void duplicateRegistrationAndBadFiltersAreRejected(){
        service.create(owner,valid("REG-001"));fails(409,()->service.create(other,valid("reg-001")));
        fails(400,()->service.list(officer,"","BOGUS"));fails(400,()->service.list(owner,"x".repeat(101),""));
    }
    @Test void invalidFieldsDoNotPersist(){
        for(Map.Entry<String,String> bad:Map.of("businessName"," ","registrationNumber","bad!","annualTurnover","-1",
                "expiryDate","2026-02-30","contactEmail","invalid","contactPhone","abcdefg").entrySet()) {
            Map<String,String> fields=valid("REG-001");fields.put(bad.getKey(),bad.getValue());fails(400,()->service.create(owner,fields));
        }
        for(String amount:List.of("NaN","1.001","10000000000000","1e999")){
            Map<String,String> fields=valid("REG-001");fields.put("annualTurnover",amount);fails(400,()->service.create(owner,fields));
        }
        assertEquals(0L,service.summary(officer).get("TOTAL"));
    }
    @Test void simultaneousDecisionsHaveOnlyOneWinner()throws Exception {
        long id=service.create(owner,valid("REG-001"));ExecutorService pool=Executors.newFixedThreadPool(2);
        CountDownLatch ready=new CountDownLatch(2),go=new CountDownLatch(1);
        try {
            List<Future<Boolean>> results=new ArrayList<>();
            for(String decision:List.of("APPROVED","REJECTED"))results.add(pool.submit(()->{
                ready.countDown();go.await();try{service.decide(officer,id,decision,"Reviewed");return true;}
                catch(WorkflowException ex){assertEquals(409,ex.getStatus());return false;}
            }));
            assertTrue(ready.await(5,TimeUnit.SECONDS));go.countDown();int wins=0;for(Future<Boolean> result:results)if(result.get(10,TimeUnit.SECONDS))wins++;
            assertEquals(1,wins);
        }finally{go.countDown();pool.shutdownNow();}
    }
    @Test void applicationSurvivesDatabaseReopen(@TempDir Path directory){
        String url="jdbc:h2:file:"+directory.resolve("saved").toString().replace('\\','/');
        long id=new LicenceService(new Database(url)).create(owner,valid("REG-PERSIST"));
        LicenceApplication saved=new LicenceService(new Database(url)).get(owner,id);
        assertEquals("REG-PERSIST",saved.getRegistrationNumber());assertEquals(ApplicationStatus.SUBMITTED,saved.getStatus());
    }
}
