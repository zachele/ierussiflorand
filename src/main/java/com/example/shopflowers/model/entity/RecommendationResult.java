package com.example.shopflowers.model.entity;

public class RecommendationResult {

    private final FlowerProduct product;
    private final String reason;
    private final int score;

    public RecommendationResult(FlowerProduct product, String reason, int score) {
        this.product = product;
        this.reason = reason;
        this.score = score;
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
