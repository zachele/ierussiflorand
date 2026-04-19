package com.example.shopflowers.model.entity;

public record RecommendationResult(
        FlowerProduct product,
        String reason,
        int score,
        boolean withinBudget
) {

    public FlowerProduct getProduct() {
        return product;
    }

    @SuppressWarnings("unused")
    public String getReason() {
        return reason;
    }

    public int getScore() {
        return score;
    }

    public boolean isWithinBudget() {
        return withinBudget;
    }

    @SuppressWarnings("unused")
    public String getBudgetCompatibility() {
        return withinBudget ? "Entro budget" : "Vicino al budget";
    }

    public String getProductName() {
        return product.getName();
    }

    public double getProductPrice() {
        return product.getPrice();
    }

    @SuppressWarnings("unused")
    public String getProductColor() {
        return product.getColor();
    }

    @SuppressWarnings("unused")
    public String getProductVariety() {
        return product.getVariety();
    }
}