package com.gov.licence.model;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
public final class LicenceApplication {
    private final long id, ownerId;
    private final String ownerName, registrationNumber, businessName, tradeCategory, contactEmail, contactPhone;
    private final BigDecimal annualTurnover;
    private final LocalDate expiryDate;
    private final ApplicationStatus status;
    private final LocalDateTime createdAt, updatedAt, decidedAt;
    private final String remarks, reviewerName;
    public LicenceApplication(long id, long ownerId, String ownerName, String registrationNumber,
            String businessName, String tradeCategory, BigDecimal annualTurnover, LocalDate expiryDate,
            String contactEmail, String contactPhone, ApplicationStatus status, LocalDateTime createdAt,
            LocalDateTime updatedAt, String remarks, String reviewerName, LocalDateTime decidedAt) {
        this.id=id; this.ownerId=ownerId; this.ownerName=ownerName; this.registrationNumber=registrationNumber;
        this.businessName=businessName; this.tradeCategory=tradeCategory; this.annualTurnover=annualTurnover;
        this.expiryDate=expiryDate; this.contactEmail=contactEmail; this.contactPhone=contactPhone;
        this.status=status; this.createdAt=createdAt; this.updatedAt=updatedAt; this.remarks=remarks;
        this.reviewerName=reviewerName; this.decidedAt=decidedAt;
    }
    public long getId(){return id;} public long getOwnerId(){return ownerId;}
    public String getOwnerName(){return ownerName;} public String getRegistrationNumber(){return registrationNumber;}
    public String getBusinessName(){return businessName;} public String getTradeCategory(){return tradeCategory;}
    public BigDecimal getAnnualTurnover(){return annualTurnover;} public LocalDate getExpiryDate(){return expiryDate;}
    public String getContactEmail(){return contactEmail;} public String getContactPhone(){return contactPhone;}
    public ApplicationStatus getStatus(){return status;} public LocalDateTime getCreatedAt(){return createdAt;}
    public LocalDateTime getUpdatedAt(){return updatedAt;} public String getRemarks(){return remarks;}
    public String getReviewerName(){return reviewerName;} public LocalDateTime getDecidedAt(){return decidedAt;}
    public boolean isEditable(){return status==ApplicationStatus.SUBMITTED;}
    public boolean isPending(){return status==ApplicationStatus.SUBMITTED || status==ApplicationStatus.UNDER_REVIEW;}
}
