package com.example.userservice.controller;

import com.example.userservice.dto.AuthDto;
import com.example.userservice.service.AuthService;
import com.example.userservice.service.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthDto.LoginResponse> login(
            @RequestBody AuthDto.LoginRequest request,
            HttpServletResponse response) {

        AuthDto.LoginResponse loginResponse = authService.login(request);

        // 쿠키에 토큰 저장
        cookieUtil.addCookie(response, "access_token", loginResponse.getAccessToken(), cookieUtil.getDaysInSeconds(1));
        cookieUtil.addCookie(response, "refresh_token", loginResponse.getRefreshToken(), cookieUtil.getDaysInSeconds(30));

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthDto.LogoutResponse> logout(
            @RequestBody AuthDto.LogoutRequest request,
            HttpServletResponse response) {

        // 쿠키 삭제
        cookieUtil.addCookie(response, "access_token", "", 0);
        cookieUtil.addCookie(response, "refresh_token", "", 0);

        // authService의 logout 메서드 호출
        return ResponseEntity.ok(authService.logout(request));
    }
}