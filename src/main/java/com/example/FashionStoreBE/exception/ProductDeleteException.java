package com.example.FashionStoreBE.exception;

public class ProductDeleteException extends RuntimeException{
    public ProductDeleteException(String message) {
        super(message);
    }
}
