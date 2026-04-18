package com.example.shopflowers.model.entity;

public record CustomBouquetOrderData(
        int orderId,
        String size,
        String packaging,
        boolean cardIncluded,
        boolean vaseIncluded,
        double totalPrice
) {

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