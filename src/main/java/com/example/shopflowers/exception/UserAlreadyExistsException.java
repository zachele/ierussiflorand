package com.example.shopflowers.exception;

public class UserAlreadyExistsException extends Exception {

    public UserAlreadyExistsException() {
        super("Username già esistente.");
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}