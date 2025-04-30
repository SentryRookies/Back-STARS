package com.example.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${service.jwt.secret-key}")
    private String secretKeyString;
    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        if (secretKeyString == null || secretKeyString.isEmpty()) {
            log.error("JWT Secret Key가 설정되지 않았습니다! 환경 변수 또는 설정 파일을 확인하세요.");
            throw new IllegalStateException("JWT_SECRET_KEY가 설정되지 않았습니다!");
        }
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        log.info("JWT SecretKey 초기화 완료");
    }

    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            log.warn("입력된 JWT 토큰이 비어 있습니다.");
            return false;
        }
        try {
            Jwts.parser()
                    .verifyWith(this.secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true; // 예외가 발생하지 않으면 유효함
        } catch (SignatureException e) {
            log.error("JWT 서명이 유효하지 않습니다.", e);
        } catch (MalformedJwtException e) {
            log.error("JWT 토큰 형식이 잘못되었습니다.", e);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰 형식입니다.", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT 클레임 문자열이 비어 있습니다.", e);
        } catch (Exception e) { // 그 외 예외 처리
            log.error("JWT 토큰 검증 중 알 수 없는 오류 발생", e);
        }
        return false; // 예외 발생 시 유효하지 않음
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(this.secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject(); // Subject (userId) 반환
    }
}
