package com.rjtmahinay.underwriting.internal_risk_engine_service.model;

public enum LoanType {
    PERSONAL("Personal Loan"),
    MORTGAGE("Mortgage Loan"),
    AUTO("Auto Loan"),
    BUSINESS("Business Loan"),
    STUDENT("Student Loan"),
    CREDIT_CARD("Credit Card");

    private final String displayName;

    LoanType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
