package com.example.be_datn.exception;

public class CustomPaymentException extends RuntimeException {
    public CustomPaymentException(String message) {
        super(message);
    }
}