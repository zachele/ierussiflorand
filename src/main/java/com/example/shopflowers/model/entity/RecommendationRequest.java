package com.example.shopflowers.model.entity;

public record RecommendationRequest(
        String occasion,
        String style,
        double maxBudget,
        String preferredColor
) {

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