package com.example.shopflowers.model.entity;

import java.util.List;

public class Order {

    private final String username;
    private final List<CartItem> items;
    private final String deliveryMode;
    private final String deliveryAddress;
    private final String pickupDate;
    private final String pickupTime;
    private final String paymentMethod;
    private final String status;
    private final double total;

    public Order(String username, List<CartItem> items, String deliveryMode, String deliveryAddress,
                 String pickupDate, String pickupTime, String paymentMethod, String status, double total) {
        this.username = username;
        this.items = items;
        this.deliveryMode = deliveryMode;
        this.deliveryAddress = deliveryAddress;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
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

    public String getPickupDate() {
        return pickupDate;
    }

    public String getPickupTime() {
        return pickupTime;
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