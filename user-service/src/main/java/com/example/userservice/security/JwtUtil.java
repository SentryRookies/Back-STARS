package com.example.userservice.security;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private SecretKey secretKey;
    private Long accessTokenExpiration; // 액세스 토큰 만료 시간 (ms)
    private Long refreshTokenExpiration; // 리프레시 토큰 만료 시간 (ms)

    @PostConstruct
    public void init() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        String jwtSecret = dotenv.get("JWT_SECRET_KEY");
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET_KEY 환경변수가 설정되지 않았습니다!");
        }

        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        // 액세스 토큰 유효 시간을 45분(2,700,000 밀리초)으로 설정
        this.accessTokenExpiration = 2700000L; // 45분
        this.refreshTokenExpiration = Long.parseLong(dotenv.get("JWT_REFRESH_EXPIRATION", "604800000")); // 기본값 7일

        System.out.println("JWT 설정 완료: 액세스 토큰 만료 시간 = " + (accessTokenExpiration / 60000) + "분");
    }

    // 사용자 이름 추출
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 만료 시간 추출
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 토큰 ID 추출 (토큰 버전 관리용)
    public String extractTokenId(String token) {
        return extractClaim(token, claims -> claims.get("tokenId", String.class));
    }

    // 클레임 추출
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 모든 클레임 추출
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 토큰 만료 확인
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 액세스 토큰 생성
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        String token = createToken(claims, userDetails.getUsername(), accessTokenExpiration);
        System.out.println("액세스 토큰 발급: 사용자 = " + userDetails.getUsername() + ", 만료 시간 = " + new Date(System.currentTimeMillis() + accessTokenExpiration));
        return token;
    }

    // 액세스 토큰 생성 (nickname 직접 전달)
    public String generateAccessToken(String nickname) {
        Map<String, Object> claims = new HashMap<>();
        String token = createToken(claims, nickname, accessTokenExpiration);
        System.out.println("액세스 토큰 발급: 사용자 = " + nickname + ", 만료 시간 = " + new Date(System.currentTimeMillis() + accessTokenExpiration));
        return token;
    }

    // 리프레시 토큰 생성
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        String token = createToken(claims, userDetails.getUsername(), refreshTokenExpiration);
        System.out.println("리프레시 토큰 발급: 사용자 = " + userDetails.getUsername() + ", 만료 시간 = " + new Date(System.currentTimeMillis() + refreshTokenExpiration));
        return token;
    }

    // 리프레시 토큰 생성 (nickname 직접 전달)
    public String generateRefreshToken(String nickname) {
        Map<String, Object> claims = new HashMap<>();
        String token = createToken(claims, nickname, refreshTokenExpiration);
        System.out.println("리프레시 토큰 발급: 사용자 = " + nickname + ", 만료 시간 = " + new Date(System.currentTimeMillis() + refreshTokenExpiration));
        return token;
    }

    // 토큰 생성 메소드
    private String createToken(Map<String, Object> claims, String subject, Long expirationTime) {
        // 토큰 ID 생성 (토큰 버전 관리를 위한 고유 식별자)
        String tokenId = String.valueOf(System.currentTimeMillis());

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .claim("tokenId", tokenId)  // 토큰 ID 추가 (버전 관리용)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // 토큰 유효성 검증 (UserDetails와 비교)
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // 토큰 유효성만 검사 (사용자 정보 없이)
    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}