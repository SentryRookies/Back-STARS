package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDto {

    /**
     * 로그인 요청 DTO
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        private String user_id;  // 로그인 시 사용자가 입력하는 ID
        private String password; // 비밀번호
    }

    /**
     * 로그인 응답 DTO
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginResponse {
        private String accessToken;   // 액세스 토큰
        private String refreshToken;  // 리프레시 토큰
        private String member_id;     // 내부 관리용 ID
        private String user_id;       // 로그인용 ID
        private String nickname;      // 닉네임
        private String role;          // 사용자 역할 (ROLE_USER or ROLE_ADMIN)
    }

    /**
     * 로그아웃 요청 DTO
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LogoutRequest {
        private String memberId;      // 회원 ID

        public String getMemberId() {
            return memberId;
        }
    }

    /**
     * 로그아웃 응답 DTO
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LogoutResponse {
        private boolean success;      // 성공 여부
        private String message;       // 메시지
    }

    /**
     * 토큰 갱신 요청 DTO
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RefreshTokenRequest {
        private String refreshToken;  // 리프레시 토큰
    }
}