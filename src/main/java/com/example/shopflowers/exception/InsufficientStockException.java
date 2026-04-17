package com.example.shopflowers.exception;

public class InsufficientStockException extends Exception {

    @SuppressWarnings("unused")
    public InsufficientStockException() {
        super("Quantità richiesta non disponibile.");
    }

    public InsufficientStockException(String message) {
        super(message);
    }
}