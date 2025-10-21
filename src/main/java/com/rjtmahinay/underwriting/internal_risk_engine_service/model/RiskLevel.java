package com.rjtmahinay.underwriting.internal_risk_engine_service.model;

public enum RiskLevel {
    LOW("Low Risk", 1, 300),
    MODERATE("Moderate Risk", 301, 500),
    HIGH("High Risk", 501, 700),
    VERY_HIGH("Very High Risk", 701, 1000);

    private final String displayName;
    private final int minScore;
    private final int maxScore;

    RiskLevel(String displayName, int minScore, int maxScore) {
        this.displayName = displayName;
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinScore() {
        return minScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public static RiskLevel fromScore(int score) {
        for (RiskLevel level : values()) {
            if (score >= level.minScore && score <= level.maxScore) {
                return level;
            }
        }
        return VERY_HIGH; // Default to highest risk if score is out of range
    }
}
