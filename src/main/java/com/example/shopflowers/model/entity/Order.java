package com.example.shopflowers.model.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Order(Builder builder) {
        this.username = builder.username;
        this.items = builder.items;
        this.deliveryMode = builder.deliveryMode;
        this.deliveryAddress = builder.deliveryAddress;
        this.pickupDate = builder.pickupDate;
        this.pickupTime = builder.pickupTime;
        this.paymentMethod = builder.paymentMethod;
        this.status = builder.status;
        this.total = builder.total;
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

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    @SuppressWarnings("unused")
    public double calculateItemsTotal() {

        if (isEmpty()) {
            return 0.0;
        }

        double calculatedTotal = 0.0;

        for (CartItem item : items) {
            calculatedTotal += item.getTotalPrice();
        }

        return calculatedTotal;
    }

    @SuppressWarnings("unused")
    public Map<Integer, Integer> getRequiredQuantities() {

        Map<Integer, Integer> requiredQuantities = new HashMap<>();

        if (isEmpty()) {
            return requiredQuantities;
        }

        for (CartItem item : items) {

            int productId = item.getProduct().getId();

            requiredQuantities.put(
                    productId,
                    requiredQuantities.getOrDefault(productId, 0)
                            + item.getQuantity()
            );
        }

        return requiredQuantities;
    }

    public static class Builder {

        private String username;
        private List<CartItem> items;
        private String deliveryMode;
        private String deliveryAddress;
        private String pickupDate;
        private String pickupTime;
        private String paymentMethod;
        private String status;
        private double total;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder items(List<CartItem> items) {
            this.items = items;
            return this;
        }

        public Builder deliveryMode(String deliveryMode) {
            this.deliveryMode = deliveryMode;
            return this;
        }

        public Builder deliveryAddress(String deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
            return this;
        }

        public Builder pickupDate(String pickupDate) {
            this.pickupDate = pickupDate;
            return this;
        }

        public Builder pickupTime(String pickupTime) {
            this.pickupTime = pickupTime;
            return this;
        }

        public Builder paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder total(double total) {
            this.total = total;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}