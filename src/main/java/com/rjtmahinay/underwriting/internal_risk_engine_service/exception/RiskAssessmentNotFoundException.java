package com.rjtmahinay.underwriting.internal_risk_engine_service.exception;

public class RiskAssessmentNotFoundException extends RuntimeException {
    
    public RiskAssessmentNotFoundException(String message) {
        super(message);
    }
    
    public RiskAssessmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public RiskAssessmentNotFoundException(Long applicationId) {
        super("Risk assessment not found for application ID: " + applicationId);
    }
}
