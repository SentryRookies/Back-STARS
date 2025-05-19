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
    private Long accessTokenExpiration;     // 액세스 토큰 유효 시간
    private Long refreshTokenExpiration;    // 리프레시 토큰 유효 시간

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
        this.accessTokenExpiration = 1000L * 60 * 45;         // 45분
        this.refreshTokenExpiration = 1000L * 60 * 60 * 6;    // 6시간

        System.out.println("JWT 설정 완료: 액세스 토큰 = " + (accessTokenExpiration / 60000) + "분, 리프레시 토큰 = " + (refreshTokenExpiration / 60000) + "분");
    }

    // 사용자 이름 추출
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 만료 시간 추출
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 토큰 ID 추출
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
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 토큰 만료 여부
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 액세스 토큰 생성 (UserDetails 기반)
    public String generateToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails.getUsername(), accessTokenExpiration);
    }

    // 액세스 토큰 생성 (닉네임 기반)
    public String generateAccessToken(String nickname) {
        return createToken(new HashMap<>(), nickname, accessTokenExpiration);
    }

    // 리프레시 토큰 생성 (UserDetails 기반)
    public String generateRefreshToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails.getUsername(), refreshTokenExpiration);
    }

    // 리프레시 토큰 생성 (닉네임 기반)
    public String generateRefreshToken(String nickname) {
        return createToken(new HashMap<>(), nickname, refreshTokenExpiration);
    }

    // JWT 생성 로직
    private String createToken(Map<String, Object> claims, String subject, Long expirationTime) {
        String tokenId = String.valueOf(System.currentTimeMillis());

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .claim("tokenId", tokenId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // 토큰 유효성 검사 (UserDetails와 비교)
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // 토큰 유효성 검사 (단순 검증)
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
