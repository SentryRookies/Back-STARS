package com.example.externalinfoservice.exception;

public class CustomExceptionClass {
    public static class DataNotFoundException extends RuntimeException {
        public DataNotFoundException(String message) {
            super(message);
        }

        public DataNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) {
            super(message);
        }

        public ConflictException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class TooManyRequestsException extends RuntimeException {
        public TooManyRequestsException(String message) {
            super(message);
        }

        public TooManyRequestsException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
