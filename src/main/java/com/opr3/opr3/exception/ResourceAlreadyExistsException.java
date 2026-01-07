package com.opr3.opr3.exception;

/**
 * Exception thrown when attempting to create a resource that already exists.
 * Results in HTTP 409 status (Conflict).
 */
public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
