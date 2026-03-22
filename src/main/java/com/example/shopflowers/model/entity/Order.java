package com.example.shopflowers.model.entity;

import java.util.List;

public class Order {

    private List<CartItem> items;
    private String deliveryMode;
    private String paymentMethod;
    private double total;

    public Order(List<CartItem> items, String deliveryMode, String paymentMethod, double total) {
        this.items = items;
        this.deliveryMode = deliveryMode;
        this.paymentMethod = paymentMethod;
        this.total = total;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}