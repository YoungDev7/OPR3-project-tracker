package com.opr3.opr3.exception;

/**
 * Exception thrown when request data is invalid.
 * Results in HTTP 400 status (Bad Request).
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
