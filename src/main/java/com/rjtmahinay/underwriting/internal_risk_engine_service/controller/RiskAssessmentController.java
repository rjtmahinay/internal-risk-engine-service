package com.rjtmahinay.underwriting.internal_risk_engine_service.controller;

import com.rjtmahinay.underwriting.internal_risk_engine_service.model.LoanApplication;
import com.rjtmahinay.underwriting.internal_risk_engine_service.model.RiskAssessment;
import com.rjtmahinay.underwriting.internal_risk_engine_service.service.RiskScoringService;
import com.rjtmahinay.underwriting.internal_risk_engine_service.service.UnderwritingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/v1/risk-assessment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Risk Assessment", description = "Endpoints for loan risk assessment and evaluation")
public class RiskAssessmentController {

    private final RiskScoringService riskScoringService;
    private final UnderwritingService underwritingService;

    @Operation(
        summary = "Evaluate loan application risk",
        description = "Analyzes a loan application and generates a comprehensive risk assessment including risk score, approval recommendation, and suggested interest rate"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Risk assessment created successfully", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RiskAssessment.class))),
        @ApiResponse(responseCode = "400", description = "Invalid loan application data", 
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/evaluate")
    public Mono<ResponseEntity<RiskAssessment>> evaluateRisk(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Loan application data to be evaluated",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoanApplication.class),
                    examples = @ExampleObject(
                        name = "Sample Loan Application",
                        value = """
                        {
                          "applicantName": "John Doe",
                          "email": "john.doe@email.com",
                          "age": 35,
                          "annualIncome": 75000,
                          "loanAmount": 250000,
                          "loanType": "MORTGAGE",
                          "loanTermMonths": 360,
                          "creditScore": 720,
                          "employmentYears": 5,
                          "monthlyDebtPayments": 1200,
                          "downPayment": 50000,
                          "hasCollateral": true,
                          "collateralValue": 300000
                        }
                        """
                    )
                )
            )
            @RequestBody LoanApplication loanData) {
        if (loanData == null) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        
        log.info("Received risk assessment request for applicant: {}", loanData.getApplicantName());
        
        return Mono.fromSupplier(() -> riskScoringService.calculateRiskAssessment(loanData))
                .flatMap(assessment -> underwritingService.saveRiskAssessment(assessment))
                .map(assessment -> ResponseEntity.status(HttpStatus.CREATED).body(assessment))
                .onErrorResume(IllegalArgumentException.class, 
                    ex -> Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(Exception.class, 
                    ex -> {
                        log.error("Error evaluating risk for application: {}", ex.getMessage(), ex);
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    });
    }

    @Operation(
        summary = "Get risk assessment by ID",
        description = "Retrieves a specific risk assessment using its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Risk assessment found", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RiskAssessment.class))),
        @ApiResponse(responseCode = "400", description = "Invalid assessment ID", 
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Risk assessment not found", 
            content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/assessments/{assessmentId}")
    public Mono<ResponseEntity<RiskAssessment>> getRiskAssessment(
            @Parameter(description = "Unique identifier of the risk assessment", required = true, example = "1")
            @PathVariable Long assessmentId) {
        if (assessmentId == null || assessmentId <= 0) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        
        log.info("Retrieving risk assessment with ID: {}", assessmentId);
        
        return underwritingService.getRiskAssessmentById(assessmentId)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Get all risk assessments",
        description = "Retrieves all risk assessments in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of risk assessments", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RiskAssessment.class)))
    })
    @GetMapping("/assessments")
    public Flux<RiskAssessment> getAllRiskAssessments() {
        log.info("Retrieving all risk assessments");
        return underwritingService.getAllRiskAssessments();
    }

    @Operation(
        summary = "Batch evaluate loan applications",
        description = "Processes multiple loan applications simultaneously and returns their risk assessments"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Batch processing completed", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RiskAssessment.class))),
        @ApiResponse(responseCode = "400", description = "Invalid batch request", 
            content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/batch-evaluate")
    public Flux<RiskAssessment> evaluateRiskBatch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Stream of loan applications to be evaluated",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = LoanApplication.class)
                )
            )
            @RequestBody Flux<LoanApplication> loanDataList) {
        log.info("Received batch risk assessment request");
        
        return loanDataList
                .filter(application -> application != null)
                .map(riskScoringService::calculateRiskAssessment)
                .flatMap(assessment -> underwritingService.saveRiskAssessment(assessment))
                .onErrorContinue((throwable, obj) -> 
                    log.error("Error processing loan application: {}", obj, throwable));
    }
}
