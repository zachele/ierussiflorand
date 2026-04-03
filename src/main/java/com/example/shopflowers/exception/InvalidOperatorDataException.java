package com.example.shopflowers.exception;

public class InvalidOperatorDataException extends Exception {

    public InvalidOperatorDataException() {
        super("Dati operatore non validi.");
    }

    public InvalidOperatorDataException(String message) {
        super(message);
    }
}