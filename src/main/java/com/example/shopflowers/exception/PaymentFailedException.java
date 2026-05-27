package com.example.shopflowers.exception;

public class PaymentFailedException extends Exception {

    public PaymentFailedException(String message) {
        super(message);
    }
}