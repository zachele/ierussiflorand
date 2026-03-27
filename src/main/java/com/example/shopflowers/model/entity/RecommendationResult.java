package com.example.shopflowers.model.entity;

public class RecommendationResult {

    private final FlowerProduct product;
    private final String reason;
    private final int score;
    private final boolean withinBudget;

    public RecommendationResult(FlowerProduct product, String reason, int score, boolean withinBudget) {
        this.product = product;
        this.reason = reason;
        this.score = score;
        this.withinBudget = withinBudget;
    }

    public FlowerProduct getProduct() {
        return product;
    }

    public String getReason() {
        return reason;
    }

    public int getScore() {
        return score;
    }

    public boolean isWithinBudget() {
        return withinBudget;
    }

    public String getBudgetCompatibility() {
        return withinBudget ? "Entro budget" : "Vicino al budget";
    }

    public String getProductName() {
        return product.getName();
    }

    public double getProductPrice() {
        return product.getPrice();
    }

    public String getProductColor() {
        return product.getColor();
    }

    public String getProductVariety() {
        return product.getVariety();
    }
}
