package com.example.shopflowers.model.entity;

public class OrderItemSummary {

    private  final String productName;
    private final int  quantity;
    private final double unitPrice;

    public OrderItemSummary(String productName, int quantity, double unitPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

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