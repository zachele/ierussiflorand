package com.example.shopflowers.model.entity;

public class CartItem {

    private FlowerProduct product;
    private int quantity;

    public CartItem(FlowerProduct product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public FlowerProduct getProduct() {
        return product;
    }

    public void setProduct(FlowerProduct product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
}