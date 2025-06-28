package com.lowes.exception;

public class EmptyException extends RuntimeException {
    public EmptyException(String message) {
        super(message);
    }
}
