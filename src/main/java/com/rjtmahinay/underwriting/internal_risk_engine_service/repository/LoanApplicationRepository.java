package com.rjtmahinay.underwriting.internal_risk_engine_service.repository;

import com.rjtmahinay.underwriting.internal_risk_engine_service.model.LoanApplication;
import com.rjtmahinay.underwriting.internal_risk_engine_service.model.LoanType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public interface LoanApplicationRepository extends ReactiveCrudRepository<LoanApplication, Long> {

    Flux<LoanApplication> findByApplicantNameContaining(String applicantName);

    Flux<LoanApplication> findByLoanType(LoanType loanType);

    Flux<LoanApplication> findByEmail(String email);

    @Query("SELECT * FROM loan_applications WHERE loan_amount >= :minAmount AND loan_amount <= :maxAmount")
    Flux<LoanApplication> findByLoanAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    @Query("SELECT * FROM loan_applications WHERE credit_score >= :minScore")
    Flux<LoanApplication> findByCreditScoreGreaterThanEqual(Integer minScore);

    @Query("SELECT COUNT(*) FROM loan_applications WHERE email = :email")
    Mono<Long> countByEmail(String email);
}
