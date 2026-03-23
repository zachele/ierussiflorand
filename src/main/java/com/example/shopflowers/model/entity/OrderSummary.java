package com.example.shopflowers.model.entity;

public class OrderSummary {

    private int id;
    private String username;
    private String deliveryMode;
    private String paymentMethod;
    private double total;
    private String orderDate;

    public OrderSummary(int id, String username, String deliveryMode, String paymentMethod, double total, String orderDate) {
        this.id = id;
        this.username = username;
        this.deliveryMode = deliveryMode;
        this.paymentMethod = paymentMethod;
        this.total = total;
        this.orderDate = orderDate;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public double getTotal() {
        return total;
    }

    public String getOrderDate() {
        return orderDate;
    }
}