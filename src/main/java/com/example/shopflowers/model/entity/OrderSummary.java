package com.example.shopflowers.model.entity;

public class OrderSummary {

    private final int id;
    private final String username;
    private final String name;
    private final String surname;
    private final String deliveryMode;
    private final String deliveryAddress;
    private final String pickupDate;
    private final String pickupTime;
    private final String paymentMethod;
    private final String status;
    private final double total;
    private final String orderDate;

    public OrderSummary(int id, String username, String name, String surname,
                        String deliveryMode, String deliveryAddress,
                        String pickupDate, String pickupTime, String paymentMethod,
                        String status, double total, String orderDate) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.deliveryMode = deliveryMode;
        this.deliveryAddress = deliveryAddress;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
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

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
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

    public String getOrderDate() {
        return orderDate;
    }
}