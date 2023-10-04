package com.example.urlshrink.exceptions;

public class DuplicateKeyErrorException extends RuntimeException{
    public DuplicateKeyErrorException(String message) {
        super(message);
    }
}
