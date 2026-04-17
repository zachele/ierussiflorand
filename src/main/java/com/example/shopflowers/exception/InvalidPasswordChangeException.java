package com.example.shopflowers.exception;

public class InvalidPasswordChangeException extends Exception {

    @SuppressWarnings("unused")
    public InvalidPasswordChangeException() {
        super("Cambio password non valido.");
    }

    public InvalidPasswordChangeException(String message) {
        super(message);
    }
}