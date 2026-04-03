package com.example.shopflowers.exception;

public class InvalidPasswordChangeException extends Exception {

    public InvalidPasswordChangeException() {
        super("Cambio password non valido.");
    }

    public InvalidPasswordChangeException(String message) {
        super(message);
    }
}