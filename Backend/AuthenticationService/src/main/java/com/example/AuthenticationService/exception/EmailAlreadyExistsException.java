package com.example.AuthenticationService.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message); // Call the parent class constructor with the message
    }
}
