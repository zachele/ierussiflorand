package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.FlowerProduct;

import java.util.ArrayList;
import java.util.List;

public class CustomerCartController {

    private final List<CartItem> cartItems = new ArrayList<>();

    public boolean addToCart(FlowerProduct product, int quantity) {
        if (quantity <= 0) {
            return false;
        }

        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                int newQuantity = item.getQuantity() + quantity;

                if (newQuantity > product.getStockQuantity()) {
                    return false;
                }

                item.setQuantity(newQuantity);
                return true;
            }
        }

        if (quantity > product.getStockQuantity()) {
            return false;
        }

        cartItems.add(new CartItem(product, quantity));
        return true;
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public double getCartTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
    }
}