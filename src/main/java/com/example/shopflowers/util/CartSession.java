package com.example.shopflowers.util;

import com.example.shopflowers.controller.application.CustomerCartController;

public final class CartSession {

    private static CustomerCartController cartController = new CustomerCartController();

    private CartSession() {
    }

    public static CustomerCartController getCartController() {
        return cartController;
    }

    public static void resetCart() {
        cartController = new CustomerCartController();
    }
}