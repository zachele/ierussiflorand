package com.example.shopflowers.model.entity;

public record OrderItemSummary(
        String productName,
        int quantity,
        double unitPrice
) {

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
}