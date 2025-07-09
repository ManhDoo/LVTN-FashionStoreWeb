package com.example.FashionStoreBE.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
