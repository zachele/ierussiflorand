package com.example.shopflowers.exception;

public class UserAlreadyExistsException extends Exception {
    @SuppressWarnings("unused")
    public UserAlreadyExistsException() {
        super("Username già esistente.");
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}