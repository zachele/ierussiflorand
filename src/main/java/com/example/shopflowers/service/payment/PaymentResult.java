package com.example.shopflowers.service.payment;

public record PaymentResult(
        boolean successful,
        String message
) {

    public boolean isSuccessful() {
        return successful;
    }

    public String getMessage() {
        return message;
    }
}