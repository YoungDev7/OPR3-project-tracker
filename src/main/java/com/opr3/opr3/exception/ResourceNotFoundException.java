package com.opr3.opr3.exception;

/**
 * Exception thrown when a requested resource is not found.
 * Results in HTTP 404 status.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
