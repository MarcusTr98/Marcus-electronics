package com.marcus.electronics.exception;

// Chuyển sang RuntimeException để không phải khai báo 'throws'
public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String message) {
        super(message);
    }
}