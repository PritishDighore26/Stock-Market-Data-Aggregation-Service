package com.pritish.exception;

public class InvalidTimeframeException extends RuntimeException {
    public InvalidTimeframeException(String message) {
        super(message);
    }
}
