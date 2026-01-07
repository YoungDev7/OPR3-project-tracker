package com.opr3.opr3.exception;

/**
 * Exception thrown when user is not authorized to access a resource.
 * Results in HTTP 401 status (Unauthorized).
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
