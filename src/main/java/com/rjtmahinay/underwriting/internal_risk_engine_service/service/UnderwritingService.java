package com.rjtmahinay.underwriting.internal_risk_engine_service.service;

import com.rjtmahinay.underwriting.internal_risk_engine_service.exception.RiskAssessmentNotFoundException;
import com.rjtmahinay.underwriting.internal_risk_engine_service.model.RiskAssessment;
import com.rjtmahinay.underwriting.internal_risk_engine_service.repository.RiskAssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnderwritingService {

    private final RiskAssessmentRepository riskAssessmentRepository;

    public Mono<RiskAssessment> getRiskAssessmentById(Long assessmentId) {
        if (assessmentId == null) {
            return Mono.error(new IllegalArgumentException("Assessment ID cannot be null"));
        }
        
        return riskAssessmentRepository.findById(assessmentId)
                .doOnNext(assessment -> log.debug("Retrieved risk assessment: {}", assessment.getId()))
                .switchIfEmpty(Mono.error(new RiskAssessmentNotFoundException("Risk assessment not found with ID: " + assessmentId)));
    }

    public Flux<RiskAssessment> getAllRiskAssessments() {
        return riskAssessmentRepository.findAll()
                .doOnNext(assessment -> log.debug("Retrieved risk assessment: {}", assessment.getId()));
    }

    public Mono<RiskAssessment> saveRiskAssessment(RiskAssessment riskAssessment) {
        if (riskAssessment == null) {
            return Mono.error(new IllegalArgumentException("Risk assessment cannot be null"));
        }
        
        log.info("Saving risk assessment with score: {}", riskAssessment.getRiskScore());
        return riskAssessmentRepository.save(riskAssessment)
                .doOnNext(saved -> log.info("Saved risk assessment with ID: {}", saved.getId()))
                .doOnError(error -> log.error("Failed to save risk assessment: {}", error.getMessage()));
    }

    // Statistics and reporting methods
    public Mono<Long> getApprovedAssessmentsCount() {
        return riskAssessmentRepository.countApprovedAssessments();
    }

    public Mono<Long> getRejectedAssessmentsCount() {
        return riskAssessmentRepository.countRejectedAssessments();
    }

    public Mono<Double> getAverageRiskScore() {
        return riskAssessmentRepository.getAverageRiskScore();
    }

    public Mono<Long> getTotalAssessmentsCount() {
        return riskAssessmentRepository.count();
    }
}
