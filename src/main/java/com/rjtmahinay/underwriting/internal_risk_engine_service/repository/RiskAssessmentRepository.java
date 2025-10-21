package com.rjtmahinay.underwriting.internal_risk_engine_service.repository;

import com.rjtmahinay.underwriting.internal_risk_engine_service.model.RiskAssessment;
import com.rjtmahinay.underwriting.internal_risk_engine_service.model.RiskLevel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RiskAssessmentRepository extends ReactiveCrudRepository<RiskAssessment, Long> {

    Mono<RiskAssessment> findByLoanApplicationId(Long loanApplicationId);

    Flux<RiskAssessment> findByRiskLevel(RiskLevel riskLevel);

    Flux<RiskAssessment> findByApprovalRecommendation(Boolean approvalRecommendation);

    @Query("SELECT * FROM risk_assessments WHERE risk_score >= :minScore AND risk_score <= :maxScore")
    Flux<RiskAssessment> findByRiskScoreBetween(Integer minScore, Integer maxScore);

    @Query("SELECT COUNT(*) FROM risk_assessments WHERE approval_recommendation = true")
    Mono<Long> countApprovedAssessments();

    @Query("SELECT COUNT(*) FROM risk_assessments WHERE approval_recommendation = false")  
    Mono<Long> countRejectedAssessments();

    @Query("SELECT AVG(risk_score) FROM risk_assessments")
    Mono<Double> getAverageRiskScore();
}
