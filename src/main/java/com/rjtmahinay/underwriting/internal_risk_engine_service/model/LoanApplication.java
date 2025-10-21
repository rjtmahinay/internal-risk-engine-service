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
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("loan_applications")
@Schema(description = "Loan application containing applicant information and loan details")
public class LoanApplication {

    @Id
    @Schema(description = "Unique identifier for the loan application", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column("applicant_name")
    @Schema(description = "Full name of the loan applicant", example = "John Doe", required = true)
    private String applicantName;

    @Schema(description = "Email address of the applicant", example = "john.doe@email.com", required = true)
    private String email;

    @Schema(description = "Age of the applicant in years", example = "35", minimum = "18", maximum = "100")
    private Integer age;

    @Column("annual_income")
    @Schema(description = "Applicant's annual income in USD", example = "75000", required = true)
    private BigDecimal annualIncome;

    @Column("loan_amount")
    @Schema(description = "Requested loan amount in USD", example = "250000", required = true)
    private BigDecimal loanAmount;

    @Column("loan_type")
    @Schema(description = "Type of loan being requested", example = "MORTGAGE", required = true)
    private LoanType loanType;

    @Column("loan_term_months")
    @Schema(description = "Loan term in months", example = "360", minimum = "1")
    private Integer loanTermMonths;

    @Column("credit_score")
    @Schema(description = "Credit score of the applicant", example = "720", minimum = "300", maximum = "850")
    private Integer creditScore;

    @Column("employment_years")
    @Schema(description = "Number of years in current employment", example = "5", minimum = "0")
    private Integer employmentYears;

    @Column("monthly_debt_payments")
    @Schema(description = "Total monthly debt payments in USD", example = "1200")
    private BigDecimal monthlyDebtPayments;

    @Column("down_payment")
    @Schema(description = "Down payment amount in USD", example = "50000")
    private BigDecimal downPayment;

    @Column("has_collateral")
    @Schema(description = "Whether the loan has collateral", example = "true")
    private Boolean hasCollateral;

    @Column("collateral_value")
    @Schema(description = "Value of collateral in USD", example = "300000")
    private BigDecimal collateralValue;

    @Column("created_at")
    @Schema(description = "Timestamp when the application was created", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Column("updated_at")
    @Schema(description = "Timestamp when the application was last updated", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
