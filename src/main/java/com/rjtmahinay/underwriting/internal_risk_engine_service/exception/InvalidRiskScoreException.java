package com.rjtmahinay.underwriting.internal_risk_engine_service.exception;

public class InvalidRiskScoreException extends RuntimeException {
    
    public InvalidRiskScoreException(String message) {
        super(message);
    }
    
    public InvalidRiskScoreException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidRiskScoreException(int score) {
        super("Invalid risk score: " + score + ". Risk score must be between 1 and 1000.");
    }
}
