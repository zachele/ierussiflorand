package com.example.shopflowers.model.entity;

public class OrderSummary {

    private int id;
    private String username;
    private String deliveryMode;
    private String deliveryAddress;
    private String paymentMethod;
    private String status;
    private double total;
    private String orderDate;

    public OrderSummary(int id, String username, String deliveryMode, String deliveryAddress,
                        String paymentMethod, String status, double total, String orderDate) {
        this.id = id;
        this.username = username;
        this.deliveryMode = deliveryMode;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.status = status;
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

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public double getTotal() {
        return total;
    }

    public String getOrderDate() {
        return orderDate;
    }
}