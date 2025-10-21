package com.rjtmahinay.underwriting.internal_risk_engine_service.controller;

import com.rjtmahinay.underwriting.internal_risk_engine_service.service.UnderwritingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Statistics", description = "Endpoints for risk assessment statistics and reporting")
public class StatisticsController {

    private final UnderwritingService underwritingService;

    @Operation(
        summary = "Get overview statistics",
        description = "Retrieves comprehensive statistics including total assessments, approval rates, rejection rates, and average risk scores"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Statistics retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Statistics Overview",
                    value = """
                    {
                      "totalAssessments": 1250,
                      "approvedAssessments": 875,
                      "rejectedAssessments": 325,
                      "pendingAssessments": 50,
                      "approvalRate": 70.0,
                      "rejectionRate": 26.0,
                      "averageRiskScore": 425.5
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/overview")
    public Mono<Map<String, Object>> getOverviewStatistics() {
        log.info("Retrieving overview statistics");

        return Mono.zip(
                underwritingService.getTotalAssessmentsCount(),
                underwritingService.getApprovedAssessmentsCount(),
                underwritingService.getRejectedAssessmentsCount(),
                underwritingService.getAverageRiskScore()
        ).map(tuple -> {
            long totalAssessments = tuple.getT1();
            long approvedAssessments = tuple.getT2();
            long rejectedAssessments = tuple.getT3();
            Double averageRiskScore = tuple.getT4();

            double approvalRate = totalAssessments > 0 ? 
                (double) approvedAssessments / totalAssessments * 100 : 0.0;
            double rejectionRate = totalAssessments > 0 ? 
                (double) rejectedAssessments / totalAssessments * 100 : 0.0;

            Map<String, Object> statistics = Map.of(
                "totalAssessments", (Object) totalAssessments,
                "approvedAssessments", (Object) approvedAssessments,
                "rejectedAssessments", (Object) rejectedAssessments,
                "pendingAssessments", (Object) Math.max(0L, totalAssessments - approvedAssessments - rejectedAssessments),
                "approvalRate", (Object) (Math.round(approvalRate * 100.0) / 100.0),
                "rejectionRate", (Object) (Math.round(rejectionRate * 100.0) / 100.0),
                "averageRiskScore", (Object) (averageRiskScore != null ? 
                    Math.round(averageRiskScore * 100.0) / 100.0 : 0.0)
            );
            return statistics;
        }).doOnError(error -> log.error("Error retrieving overview statistics: {}", error.getMessage()));
    }

    @Operation(
        summary = "Get total assessments count",
        description = "Returns the total number of risk assessments in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total count retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "integer", format = "int64", example = "1250")))
    })
    @GetMapping("/assessments/count")
    public Mono<Long> getTotalAssessmentsCount() {
        log.info("Retrieving total assessments count");
        return underwritingService.getTotalAssessmentsCount();
    }

    @Operation(
        summary = "Get approved assessments count",
        description = "Returns the number of approved risk assessments"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Approved count retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "integer", format = "int64", example = "875")))
    })
    @GetMapping("/assessments/approved/count")
    public Mono<Long> getApprovedAssessmentsCount() {
        log.info("Retrieving approved assessments count");
        return underwritingService.getApprovedAssessmentsCount();
    }

    @Operation(
        summary = "Get rejected assessments count",
        description = "Returns the number of rejected risk assessments"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rejected count retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "integer", format = "int64", example = "325")))
    })
    @GetMapping("/assessments/rejected/count")
    public Mono<Long> getRejectedAssessmentsCount() {
        log.info("Retrieving rejected assessments count");
        return underwritingService.getRejectedAssessmentsCount();
    }

    @Operation(
        summary = "Get average risk score",
        description = "Returns the average risk score across all assessments"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Average risk score retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "number", format = "double", example = "425.5")))
    })
    @GetMapping("/risk-score/average")
    public Mono<Double> getAverageRiskScore() {
        log.info("Retrieving average risk score");
        return underwritingService.getAverageRiskScore();
    }
}
