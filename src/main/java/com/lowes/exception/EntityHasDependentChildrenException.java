package com.lowes.exception;

public class EntityHasDependentChildrenException extends RuntimeException {
    public EntityHasDependentChildrenException(String message) {
        super(message);
    }
}
