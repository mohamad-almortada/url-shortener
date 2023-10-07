package com.example.urlshrink.exceptions;

public class UrlValidationException extends RuntimeException{
    public UrlValidationException(String message) {
        super(message);
    }
}
