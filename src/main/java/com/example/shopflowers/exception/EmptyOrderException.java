package com.example.shopflowers.exception;

@SuppressWarnings("unused")
public class EmptyOrderException extends Exception {

    public EmptyOrderException() {
        super("Ordine vuoto.");
    }

    public EmptyOrderException(String message) {
        super(message);
    }
}