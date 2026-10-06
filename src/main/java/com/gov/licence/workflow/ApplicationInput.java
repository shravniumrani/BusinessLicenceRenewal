package com.gov.licence.workflow;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;
public record ApplicationInput(String businessName, String registrationNumber, String tradeCategory,
        BigDecimal annualTurnover, LocalDate expiryDate, String contactEmail, String contactPhone) {
    public static ApplicationInput parse(Map<String,String> fields) {
        String name=text(fields,"businessName",150), reg=text(fields,"registrationNumber",40).toUpperCase(Locale.ROOT);
        String category=text(fields,"tradeCategory",80), email=text(fields,"contactEmail",120), phone=text(fields,"contactPhone",20);
        if(!reg.matches("[A-Z0-9][A-Z0-9-]{2,39}")) fail("Registration number must contain 3–40 letters, digits or hyphens.");
        if(!email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) fail("Enter a valid contact email.");
        if(!phone.matches("[+0-9 ()-]{7,20}") || phone.replaceAll("[^0-9]", "").length()<7) fail("Enter a valid contact phone number.");
        BigDecimal turnover;
        try { turnover=new BigDecimal(text(fields,"annualTurnover",20)); }
        catch(NumberFormatException ex){throw new WorkflowException(400,"Enter a valid annual turnover.");}
        if(turnover.signum()<0 || turnover.compareTo(new BigDecimal("9999999999999.99"))>0 || Math.max(0,turnover.scale())>2)
            fail("Turnover must be non-negative, at most 9999999999999.99, with at most two decimal places.");
        LocalDate expiry;
        try { expiry=LocalDate.parse(text(fields,"expiryDate",10)); }
        catch(DateTimeParseException ex){throw new WorkflowException(400,"Enter a valid licence expiry date.");}
        if(expiry.isBefore(LocalDate.of(2000,1,1)) || expiry.isAfter(LocalDate.now().plusYears(20))) fail("Expiry date must be between 2000 and twenty years from today.");
        return new ApplicationInput(name,reg,category,turnover,expiry,email,phone);
    }
    private static String text(Map<String,String> f,String key,int max){
        String value=f.getOrDefault(key,""); value=value==null?"":value.trim();
        if(value.isEmpty() || value.length()>max || value.chars().anyMatch(c -> c<32 || c==127)) fail("Complete " + key + " within the allowed length.");
        return value;
    }
    public static String remarks(String value){
        if(value==null || value.trim().isEmpty() || value.trim().length()>1000) fail("Remarks are mandatory and must be at most 1000 characters.");
        return value.trim();
    }
    private static void fail(String message){throw new WorkflowException(400,message);}
}
