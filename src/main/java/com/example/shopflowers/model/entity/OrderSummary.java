package com.example.shopflowers.model.entity;

public record OrderSummary(
        int id,
        String username,
        String name,
        String surname,
        String deliveryMode,
        String deliveryAddress,
        String pickupDate,
        String pickupTime,
        String paymentMethod,
        String status,
        double total,
        String orderDate
) {

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