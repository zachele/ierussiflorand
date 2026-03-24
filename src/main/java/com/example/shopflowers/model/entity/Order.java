package com.example.shopflowers.model.entity;

import java.util.List;

public class Order {

    private String username;
    private List<CartItem> items;
    private String deliveryMode;
    private String deliveryAddress;
    private String paymentMethod;
    private String status;
    private double total;

    public Order(String username, List<CartItem> items, String deliveryMode, String deliveryAddress,
                 String paymentMethod, String status, double total) {
        this.username = username;
        this.items = items;
        this.deliveryMode = deliveryMode;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.total = total;
    }

    public String getUsername() {
        return username;
    }

    public List<CartItem> getItems() {
        return items;
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
}