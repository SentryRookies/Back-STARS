package com.example.userservice.config;

import com.example.userservice.security.JwtUtil;
import com.example.userservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 1. 인증 성공했을 때 authentication 객체를 이용해서
        // 2. accessToken, refreshToken 생성
        String accessToken = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());
        String refreshToken = jwtUtil.generateRefreshToken((UserDetails) authentication.getPrincipal());

        // 3. Redis에 Refresh Token 저장
        String userId = authentication.getName();  // OAuth2 로그인 시 기본적으로 userId 같은 값
        tokenService.saveRefreshToken(Long.parseLong(userId), refreshToken); // userId가 Long 타입이면

        // 4. 응답에 AccessToken, RefreshToken 보내주기 (예시)
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"accessToken\": \"" + accessToken + "\", \"refreshToken\": \"" + refreshToken + "\"}");
    }
}
