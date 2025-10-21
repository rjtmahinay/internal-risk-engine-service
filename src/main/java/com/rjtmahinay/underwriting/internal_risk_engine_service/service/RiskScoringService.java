package com.rjtmahinay.underwriting.internal_risk_engine_service.service;

import com.rjtmahinay.underwriting.internal_risk_engine_service.model.LoanApplication;
import com.rjtmahinay.underwriting.internal_risk_engine_service.model.LoanType;
import com.rjtmahinay.underwriting.internal_risk_engine_service.model.RiskAssessment;
import com.rjtmahinay.underwriting.internal_risk_engine_service.model.RiskLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@Slf4j
public class RiskScoringService {

    // Business rule constants
    private static final BigDecimal MAX_DTI_RATIO = BigDecimal.valueOf(0.43); // 43% max debt-to-income
    private static final int MIN_CREDIT_SCORE = 300;
    private static final int MAX_CREDIT_SCORE = 850;
    private static final int SCALE = 4; // Precision for BigDecimal calculations

    public RiskAssessment calculateRiskAssessment(LoanApplication application) {
        if (application == null) {
            throw new IllegalArgumentException("Loan application cannot be null");
        }
        
        log.info("Calculating risk assessment for application ID: {}", application.getId());

        // Calculate individual risk factors
        int creditScoreFactor = calculateCreditScoreFactor(application.getCreditScore());
        int incomeFactor = calculateIncomeFactor(application);
        int employmentFactor = calculateEmploymentFactor(application.getEmploymentYears());
        int collateralFactor = calculateCollateralFactor(application);
        int loanTypeFactor = calculateLoanTypeFactor(application.getLoanType());

        // Calculate total risk score (lower is better)
        int totalRiskScore = creditScoreFactor + incomeFactor + employmentFactor + 
                            collateralFactor + loanTypeFactor;

        // Determine risk level and approval recommendation
        RiskLevel riskLevel = RiskLevel.fromScore(totalRiskScore);
        boolean approvalRecommendation = determineApprovalRecommendation(totalRiskScore, application);
        
        // Calculate recommended interest rate
        BigDecimal recommendedRate = calculateInterestRate(totalRiskScore, application.getLoanType());

        // Calculate financial ratios
        BigDecimal debtToIncomeRatio = calculateDebtToIncomeRatio(application);
        BigDecimal loanToValueRatio = calculateLoanToValueRatio(application);

        // Generate assessment notes
        String assessmentNotes = generateAssessmentNotes(application, totalRiskScore, riskLevel);

        return RiskAssessment.builder()
                .loanApplicationId(application.getId())
                .riskScore(totalRiskScore)
                .riskLevel(riskLevel)
                .approvalRecommendation(approvalRecommendation)
                .recommendedInterestRate(recommendedRate)
                .debtToIncomeRatio(debtToIncomeRatio)
                .loanToValueRatio(loanToValueRatio)
                .creditScoreFactor(creditScoreFactor)
                .incomeFactor(incomeFactor)
                .employmentFactor(employmentFactor)
                .collateralFactor(collateralFactor)
                .loanTypeFactor(loanTypeFactor)
                .assessmentNotes(assessmentNotes)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private int calculateCreditScoreFactor(Integer creditScore) {
        if (creditScore == null) {
            return 200; // High risk if no credit score
        }

        if (creditScore >= 750) return 50;   // Excellent
        if (creditScore >= 700) return 100;  // Good
        if (creditScore >= 650) return 150;  // Fair
        if (creditScore >= 600) return 200;  // Poor
        return 250; // Very Poor
    }

    private int calculateIncomeFactor(LoanApplication application) {
        if (application.getAnnualIncome() == null || application.getLoanAmount() == null) {
            return 200;
        }

        // Calculate income to loan ratio
        BigDecimal incomeToLoanRatio = application.getAnnualIncome()
                .divide(application.getLoanAmount(), SCALE, RoundingMode.HALF_UP);

        if (incomeToLoanRatio.compareTo(BigDecimal.valueOf(3.0)) >= 0) return 50;   // Very good
        if (incomeToLoanRatio.compareTo(BigDecimal.valueOf(2.0)) >= 0) return 100;  // Good
        if (incomeToLoanRatio.compareTo(BigDecimal.valueOf(1.5)) >= 0) return 150;  // Fair
        if (incomeToLoanRatio.compareTo(BigDecimal.valueOf(1.0)) >= 0) return 200;  // Poor
        return 250; // Very poor
    }

    private int calculateEmploymentFactor(Integer employmentYears) {
        if (employmentYears == null) {
            return 150;
        }

        if (employmentYears >= 5) return 50;   // Stable employment
        if (employmentYears >= 2) return 100;  // Good employment history
        if (employmentYears >= 1) return 150;  // Recent employment
        return 200; // New employment
    }

    private int calculateCollateralFactor(LoanApplication application) {
        if (Boolean.TRUE.equals(application.getHasCollateral()) && 
            application.getCollateralValue() != null &&
            application.getLoanAmount() != null) {
            
            BigDecimal collateralRatio = application.getCollateralValue()
                    .divide(application.getLoanAmount(), SCALE, RoundingMode.HALF_UP);
            
            if (collateralRatio.compareTo(BigDecimal.valueOf(1.5)) >= 0) return 25;   // Excellent collateral
            if (collateralRatio.compareTo(BigDecimal.valueOf(1.2)) >= 0) return 50;   // Good collateral
            if (collateralRatio.compareTo(BigDecimal.valueOf(1.0)) >= 0) return 75;   // Adequate collateral
            return 100; // Insufficient collateral
        }
        
        return 150; // No collateral
    }

    private int calculateLoanTypeFactor(LoanType loanType) {
        return switch (loanType) {
            case MORTGAGE -> 50;      // Secured by property
            case AUTO -> 75;          // Secured by vehicle
            case STUDENT -> 100;      // Education investment
            case BUSINESS -> 125;     // Higher risk business loan
            case PERSONAL -> 150;     // Unsecured personal loan
            case CREDIT_CARD -> 175;  // Highest risk unsecured
        };
    }

    private boolean determineApprovalRecommendation(int riskScore, LoanApplication application) {
        // Basic approval logic
        if (riskScore <= 300) return true;   // Low risk - approve
        if (riskScore <= 500) {              // Moderate risk - conditional approval
            return hasAcceptableDebtToIncomeRatio(application);
        }
        return false; // High/Very high risk - reject
    }

    private boolean hasAcceptableDebtToIncomeRatio(LoanApplication application) {
        BigDecimal dtiRatio = calculateDebtToIncomeRatio(application);
        return dtiRatio.compareTo(MAX_DTI_RATIO) <= 0;
    }

    private BigDecimal calculateInterestRate(int riskScore, LoanType loanType) {
        // Base rates by loan type
        BigDecimal baseRate = switch (loanType) {
            case MORTGAGE -> BigDecimal.valueOf(3.5);
            case AUTO -> BigDecimal.valueOf(4.0);
            case STUDENT -> BigDecimal.valueOf(5.0);
            case BUSINESS -> BigDecimal.valueOf(6.0);
            case PERSONAL -> BigDecimal.valueOf(8.0);
            case CREDIT_CARD -> BigDecimal.valueOf(15.0);
        };

        // Add risk premium based on score
        BigDecimal riskPremium = BigDecimal.ZERO;
        if (riskScore > 700) riskPremium = BigDecimal.valueOf(8.0);
        else if (riskScore > 500) riskPremium = BigDecimal.valueOf(5.0);
        else if (riskScore > 300) riskPremium = BigDecimal.valueOf(2.0);
        else riskPremium = BigDecimal.valueOf(1.0);

        return baseRate.add(riskPremium).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDebtToIncomeRatio(LoanApplication application) {
        if (application.getAnnualIncome() == null || application.getMonthlyDebtPayments() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal monthlyIncome = application.getAnnualIncome().divide(BigDecimal.valueOf(12), SCALE, RoundingMode.HALF_UP);
        if (monthlyIncome.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return application.getMonthlyDebtPayments()
                .divide(monthlyIncome, SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateLoanToValueRatio(LoanApplication application) {
        if (application.getLoanAmount() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal propertyValue = application.getLoanAmount();
        if (application.getDownPayment() != null) {
            propertyValue = application.getLoanAmount().add(application.getDownPayment());
        }

        if (propertyValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE;
        }

        return application.getLoanAmount()
                .divide(propertyValue, SCALE, RoundingMode.HALF_UP);
    }

    private String generateAssessmentNotes(LoanApplication application, int riskScore, RiskLevel riskLevel) {
        StringBuilder notes = new StringBuilder();
        notes.append("Risk Assessment Summary:\n");
        notes.append("- Overall Risk Score: ").append(riskScore).append(" (").append(riskLevel.getDisplayName()).append(")\n");
        
        if (application.getCreditScore() != null) {
            if (application.getCreditScore() >= 750) {
                notes.append("- Excellent credit score (").append(application.getCreditScore()).append(")\n");
            } else if (application.getCreditScore() < 600) {
                notes.append("- Poor credit score (").append(application.getCreditScore()).append(") - major risk factor\n");
            }
        }

        BigDecimal dtiRatio = calculateDebtToIncomeRatio(application);
        if (dtiRatio.compareTo(MAX_DTI_RATIO) > 0) {
            notes.append("- High debt-to-income ratio (").append(dtiRatio.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP)).append("%)\n");
        }

        if (Boolean.TRUE.equals(application.getHasCollateral())) {
            notes.append("- Loan secured with collateral\n");
        } else {
            notes.append("- Unsecured loan increases risk\n");
        }

        return notes.toString();
    }
}
