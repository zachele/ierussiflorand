package com.example.shopflowers.model.entity;

public class CustomBouquetOrderData {

    private final int orderId;
    private final String size;
    private final String packaging;
    private final boolean cardIncluded;
    private final boolean vaseIncluded;
    private final double totalPrice;

    public CustomBouquetOrderData(int orderId, String size, String packaging,
                                  boolean cardIncluded, boolean vaseIncluded, double totalPrice) {
        this.orderId = orderId;
        this.size = size;
        this.packaging = packaging;
        this.cardIncluded = cardIncluded;
        this.vaseIncluded = vaseIncluded;
        this.totalPrice = totalPrice;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getSize() {
        return size;
    }

    public String getPackaging() {
        return packaging;
    }

    public boolean isCardIncluded() {
        return cardIncluded;
    }

    public boolean isVaseIncluded() {
        return vaseIncluded;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}