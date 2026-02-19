package com.example.amit.exception;

public abstract class AppException extends RuntimeException {
    public AppException(String message) {
        super(message);
    }
}

