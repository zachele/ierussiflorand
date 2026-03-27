package com.example.shopflowers.model.entity;

public class RecommendationRequest{

    private final String occasion;
    private final String style;
    private final double maxBudget;
    private final String preferredColor;

    public RecommendationRequest(String occasion, String style, double maxBudget, String preferredColor) {
        this.occasion = occasion;
        this.style = style;
        this.maxBudget = maxBudget;
        this.preferredColor = preferredColor;
    }

    public String getOccasion() {
        return occasion;
    }

    public String getStyle() {
        return style;
    }

    public double getMaxBudget() {
        return maxBudget;
    }

    public String getPreferredColor() {
        return preferredColor;
    }
}
