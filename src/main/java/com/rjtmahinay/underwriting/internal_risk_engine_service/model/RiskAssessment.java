package com.rjtmahinay.underwriting.internal_risk_engine_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("risk_assessments")
@Schema(description = "Risk assessment result containing risk analysis and recommendations")
public class RiskAssessment {

    @Id
    @Schema(description = "Unique identifier for the risk assessment", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column("loan_application_id")
    @Schema(description = "ID of the associated loan application", example = "1")
    private Long loanApplicationId;

    @Column("risk_score")
    @Schema(description = "Calculated risk score (lower is better)", example = "425", minimum = "1", maximum = "1000")
    private Integer riskScore;

    @Column("risk_level")
    @Schema(description = "Risk level based on the risk score", example = "MODERATE")
    private RiskLevel riskLevel;

    @Column("approval_recommendation")
    @Schema(description = "Whether the loan is recommended for approval", example = "true")
    private Boolean approvalRecommendation;

    @Column("recommended_interest_rate")
    @Schema(description = "Recommended interest rate as a percentage", example = "5.5")
    private BigDecimal recommendedInterestRate;

    @Column("debt_to_income_ratio")
    @Schema(description = "Applicant's debt-to-income ratio", example = "0.35")
    private BigDecimal debtToIncomeRatio;

    @Column("loan_to_value_ratio")
    @Schema(description = "Loan-to-value ratio", example = "0.8")
    private BigDecimal loanToValueRatio;

    @Column("credit_score_factor")
    @Schema(description = "Risk factor based on credit score", example = "100")
    private Integer creditScoreFactor;

    @Column("income_factor")
    @Schema(description = "Risk factor based on income", example = "100")
    private Integer incomeFactor;

    @Column("employment_factor")
    @Schema(description = "Risk factor based on employment history", example = "100")
    private Integer employmentFactor;

    @Column("collateral_factor")
    @Schema(description = "Risk factor based on collateral", example = "50")
    private Integer collateralFactor;

    @Column("loan_type_factor")
    @Schema(description = "Risk factor based on loan type", example = "75")
    private Integer loanTypeFactor;

    @Column("assessment_notes")
    @Schema(description = "Detailed notes about the risk assessment")
    private String assessmentNotes;

    @Column("created_at")
    @Schema(description = "Timestamp when the assessment was created", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    /**
     * Calculates the monthly payment for a loan using the formula:
     * M = P * r * (1 + r)^n / ((1 + r)^n - 1)
     * where M = monthly payment, P = principal, r = monthly rate, n = number of payments
     *
     * @param loanAmount the principal loan amount
     * @param termMonths the loan term in months
     * @return the calculated monthly payment, or zero if inputs are invalid
     */
    public BigDecimal calculateMonthlyPayment(BigDecimal loanAmount, Integer termMonths) {
        if (recommendedInterestRate == null || loanAmount == null || termMonths == null || 
            loanAmount.compareTo(BigDecimal.ZERO) <= 0 || termMonths <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal monthlyRate = recommendedInterestRate
            .divide(BigDecimal.valueOf(100), MathContext.DECIMAL128)
            .divide(BigDecimal.valueOf(12), MathContext.DECIMAL128);
            
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return loanAmount.divide(BigDecimal.valueOf(termMonths), RoundingMode.HALF_UP);
        }
        
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRatePowN = onePlusRate.pow(termMonths, MathContext.DECIMAL128);
        
        BigDecimal numerator = loanAmount.multiply(monthlyRate).multiply(onePlusRatePowN);
        BigDecimal denominator = onePlusRatePowN.subtract(BigDecimal.ONE);
        
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}
