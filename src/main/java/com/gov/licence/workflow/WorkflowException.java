package com.gov.licence.workflow;
public final class WorkflowException extends RuntimeException {
    private final int status;
    public WorkflowException(int status, String message){super(message);this.status=status;}
    public int getStatus(){return status;}
}
