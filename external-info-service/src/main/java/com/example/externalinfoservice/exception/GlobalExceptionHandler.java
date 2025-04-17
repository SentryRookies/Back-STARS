package com.example.externalinfoservice.exception;

import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.naming.ServiceUnavailableException;
import javax.xml.bind.ValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Spring에서 제공하는 기본 예외 클래스

    // 400 - 1000 - InvalidRequestException
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException ex) {
        return buildErrorResponse( ErrorCode.INVALID_REQUEST, ex.getMessage());
    }
    // 401 - 2000 - Unauthorized
    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(HttpClientErrorException.Unauthorized ex) {
        return buildErrorResponse( ErrorCode.UNAUTHORIZED, ex.getMessage());
    }
    // 403 - 2001 - Forbidden
    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(HttpClientErrorException.Forbidden ex) {
        return buildErrorResponse(ErrorCode.FORBIDDEN, ex.getMessage());
    }
    // 404 - 3000 - Resource not found
    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(HttpClientErrorException.NotFound ex) {
        return buildErrorResponse(ErrorCode.RESOURCE_NOT_FOUND, ex.getMessage());
    }

    // 커스텀 예외 클래스
    // 400 - 1001 - DataNotFoundException
    @ExceptionHandler(CustomExceptionClass.DataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDataNotFoundException(CustomExceptionClass.DataNotFoundException ex) {
        return buildErrorResponse(ErrorCode.DATA_NOT_FOUND, ex.getMessage());
    }

    // 409 - 3001 - Conflict
    @ExceptionHandler(CustomExceptionClass.ConflictException.class) // 커스텀 예외
    public ResponseEntity<ErrorResponse> handleConflictException(CustomExceptionClass.ConflictException ex) {
        return buildErrorResponse(ErrorCode.CONFLICT, ex.getMessage());
    }

    // 422 - 3002 - Validation failed
    @ExceptionHandler(ValidationException.class) // javax.validation 또는 커스텀 가능
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        return buildErrorResponse(ErrorCode.VALIDATION_FAILED, ex.getMessage());
    }

    // 429 - 3003 - Too Many Requests
    @ExceptionHandler(CustomExceptionClass.TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequestsException(CustomExceptionClass.TooManyRequestsException ex) {
        return buildErrorResponse(ErrorCode.TOO_MANY_REQUESTS, ex.getMessage());
    }

    // 500 - 9000 - Internal server error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception ex) {
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    // 503 - 9001 - Service unavailable
    @ExceptionHandler(ServiceUnavailableException.class) // 커스텀 예외
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(ServiceUnavailableException ex) {
        return buildErrorResponse(ErrorCode.SERVICE_UNAVAILABLE, ex.getMessage());
    }


    // === 공통 응답 생성 메서드 ===
    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCode errorCode, String message) {
        return new ResponseEntity<>(
                new ErrorResponse(errorCode.getStatus().value(), errorCode.getCode(), message),
                errorCode.getStatus()
        );
    }




}
