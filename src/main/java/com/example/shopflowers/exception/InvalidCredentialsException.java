package com.example.shopflowers.exception;

public class InvalidCredentialsException extends Exception {
    @SuppressWarnings("unused")
    public InvalidCredentialsException() {
        super("Credenziali non valide.");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}