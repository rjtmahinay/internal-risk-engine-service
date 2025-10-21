package com.rjtmahinay.underwriting.internal_risk_engine_service.exception;

public class DuplicateApplicationException extends RuntimeException {
    
    public DuplicateApplicationException(String message) {
        super(message);
    }
    
    public DuplicateApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static DuplicateApplicationException forEmail(String email) {
        return new DuplicateApplicationException("An active loan application already exists for email: " + email);
    }
}
