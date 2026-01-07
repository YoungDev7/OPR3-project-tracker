package com.opr3.opr3.exception;

/**
 * Exception thrown when user doesn't have permission to perform an action.
 * Results in HTTP 403 status (Forbidden).
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
