package com.example.externalinfoservice.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 400 - 1000 - Invalid request
    INVALID_REQUEST("1000", HttpStatus.BAD_REQUEST),
    DATA_NOT_FOUND("1001", HttpStatus.BAD_REQUEST),

    // 401 - 2000 - Unauthorized
    UNAUTHORIZED("2000", HttpStatus.UNAUTHORIZED),

    // 403 - 2001 - Forbidden
    FORBIDDEN("2001", HttpStatus.FORBIDDEN),

    // 404 - 3000 - Resource not found
    RESOURCE_NOT_FOUND("3000", HttpStatus.NOT_FOUND),

    // 409 - 3001 - Conflict
    CONFLICT("3001", HttpStatus.CONFLICT),

    // 422 - 3002 - Validation failed
    VALIDATION_FAILED("3002", HttpStatus.UNPROCESSABLE_ENTITY),

    // 429 - 3003 - Too Many Requests
    TOO_MANY_REQUESTS("3003", HttpStatus.TOO_MANY_REQUESTS),

    // 500 - 9000 - Internal server error
    INTERNAL_SERVER_ERROR("9000", HttpStatus.INTERNAL_SERVER_ERROR),

    // 503 - 9001 - Service unavailable
    SERVICE_UNAVAILABLE("9001", HttpStatus.SERVICE_UNAVAILABLE);

    private final String code;
    private final HttpStatus status;

    ErrorCode(String code, HttpStatus status) {
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}