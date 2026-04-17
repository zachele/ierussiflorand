package com.example.shopflowers.exception;

public class InvalidOperatorDataException extends Exception {

    @SuppressWarnings("unused")
    public InvalidOperatorDataException() {
        super("Dati operatore non validi.");
    }

    public InvalidOperatorDataException(String message) {
        super(message);
    }
}