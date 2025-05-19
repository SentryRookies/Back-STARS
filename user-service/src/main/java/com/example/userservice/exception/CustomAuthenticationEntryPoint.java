package com.example.userservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
        throws IOException {

        // 원인 예외 메시지 얻기
        Throwable cause = authException.getCause();
        String detailedMessage = authException.getMessage();
        if (cause != null) {
            detailedMessage += " | 원인: " + cause.getMessage();
        }
        // 로그에 예외 내용 남기기
        System.out.println("인증 실패 발생:"+detailedMessage+", 요청 URI: " + request.getRequestURI() + authException.getMessage()+ request.getRequestURI()+ authException);

        sendErrorResponse(response, HttpStatus.UNAUTHORIZED, detailedMessage, request.getRequestURI());
    }


    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message, String path)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
