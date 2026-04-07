package com.example.shopflowers.controller.application;

import com.example.shopflowers.exception.EmptyCartException;
import com.example.shopflowers.exception.InvalidQuantityException;
import com.example.shopflowers.exception.ProductNotFoundException;
import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.FlowerProduct;

import java.util.ArrayList;
import java.util.List;

public class CustomerCartController {

    private final List<CartItem> cartItems = new ArrayList<>();

    public boolean addToCart(FlowerProduct product, int quantity) throws InvalidQuantityException {
        if (product == null) {
            throw new InvalidQuantityException("Prodotto non valido.");
        }

        if (quantity <= 0) {
            throw new InvalidQuantityException("La quantità deve essere maggiore di zero.");
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

    public double getCartTotal() throws EmptyCartException {
        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Il carrello è vuoto.");
        }

        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }

    public void removeFromCart(int productId) throws ProductNotFoundException {
        boolean removed = cartItems.removeIf(item -> item.getProduct().getId() == productId);

        if (!removed) {
            throw new ProductNotFoundException("Prodotto non presente nel carrello.");
        }
    }

    public void clearCart() throws EmptyCartException {
        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Il carrello è già vuoto.");
        }

        cartItems.clear();
    }
}