-- Database schema for Internal Risk Engine Service

-- Create loan_applications table
CREATE TABLE IF NOT EXISTS loan_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    applicant_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    age INTEGER NOT NULL CHECK (age >= 18 AND age <= 100),
    annual_income DECIMAL(15, 2) NOT NULL CHECK (annual_income > 0),
    loan_amount DECIMAL(15, 2) NOT NULL CHECK (loan_amount >= 1000),
    loan_type VARCHAR(50) NOT NULL,
    loan_term_months INTEGER NOT NULL CHECK (loan_term_months >= 1 AND loan_term_months <= 480),
    credit_score INTEGER CHECK (credit_score >= 300 AND credit_score <= 850),
    employment_years INTEGER CHECK (employment_years >= 0),
    monthly_debt_payments DECIMAL(15, 2) DEFAULT 0 CHECK (monthly_debt_payments >= 0),
    down_payment DECIMAL(15, 2) DEFAULT 0 CHECK (down_payment >= 0),
    has_collateral BOOLEAN DEFAULT FALSE,
    collateral_value DECIMAL(15, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create risk_assessments table
CREATE TABLE IF NOT EXISTS risk_assessments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_application_id BIGINT NOT NULL,
    risk_score INTEGER NOT NULL CHECK (risk_score >= 1 AND risk_score <= 1000),
    risk_level VARCHAR(20) NOT NULL,
    approval_recommendation BOOLEAN NOT NULL,
    recommended_interest_rate DECIMAL(5, 2) NOT NULL CHECK (recommended_interest_rate >= 0),
    debt_to_income_ratio DECIMAL(5, 4) DEFAULT 0,
    loan_to_value_ratio DECIMAL(5, 4) DEFAULT 0,
    credit_score_factor INTEGER DEFAULT 0,
    income_factor INTEGER DEFAULT 0,
    employment_factor INTEGER DEFAULT 0,
    collateral_factor INTEGER DEFAULT 0,
    loan_type_factor INTEGER DEFAULT 0,
    assessment_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (loan_application_id) REFERENCES loan_applications(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_loan_applications_email ON loan_applications(email);
CREATE INDEX IF NOT EXISTS idx_loan_applications_loan_type ON loan_applications(loan_type);
CREATE INDEX IF NOT EXISTS idx_loan_applications_created_at ON loan_applications(created_at);

CREATE INDEX IF NOT EXISTS idx_risk_assessments_loan_application_id ON risk_assessments(loan_application_id);
CREATE INDEX IF NOT EXISTS idx_risk_assessments_risk_level ON risk_assessments(risk_level);
CREATE INDEX IF NOT EXISTS idx_risk_assessments_approval_recommendation ON risk_assessments(approval_recommendation);
CREATE INDEX IF NOT EXISTS idx_risk_assessments_created_at ON risk_assessments(created_at);
