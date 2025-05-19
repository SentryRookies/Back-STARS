package com.example.userservice.jwt;

import io.github.cdimascio.dotenv.Dotenv; // .env 파일에서 환경변수를 로드하기 위한 라이브러리
import io.jsonwebtoken.Jwts; // JWT 생성 및 파싱을 위한 라이브러리
import io.jsonwebtoken.SignatureAlgorithm; // 서명 알고리즘 정의
import io.jsonwebtoken.security.Keys; // 안전한 키 생성을 위한 도구
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private SecretKey secretKey;

    @PostConstruct // Bean 생성 후 자동으로 호출되는 메서드
    public void init() {
        // .env 파일에서 환경변수를 로드
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();


        String jwtSecret = dotenv.get("JWT_SECRET_KEY");


        if (jwtSecret == null) {
            throw new IllegalStateException("JWT_SECRET_KEY 환경변수가 설정되지 않았습니다!");
        }


        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Access Token 생성
    public String generateAccessToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 1000 * 60 * 45);

        // JWT 생성
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // Refresh Token 생성
    public String generateRefreshToken(String userId) {
        Date now = new Date(); // 현재 시간
        Date expiryDate = new Date(now.getTime() + 1000 * 60 * 45);

        // JWT 생성
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }
}
