package com.rjtmahinay.underwriting.internal_risk_engine_service.exception;

public class LoanApplicationNotFoundException extends RuntimeException {
    
    public LoanApplicationNotFoundException(String message) {
        super(message);
    }
    
    public LoanApplicationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public LoanApplicationNotFoundException(Long id) {
        super("Loan application not found with ID: " + id);
    }
}
