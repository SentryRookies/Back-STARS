package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class MemberDto {

    // 회원 정보 응답 DTO
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberResponse {
        private String user_id;       // 로그인 ID
        private String nickname;      // 닉네임
        private Short birth_year;     // 출생연도
        private String mbti;          // MBTI 유형
        private String gender;        // 성별
        private String role;          // 역할 (ROLE_USER, ROLE_ADMIN)
        private LocalDateTime created_at; // 계정 생성일
    }

    // 프로필 조회 응답 DTO
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfileResponse {
        private String nickname;      // 닉네임
        private Short birth_year;     // 출생연도
        private String mbti;          // MBTI 유형
        private String gender;        // 성별
        private String role;          // 역할 (ROLE_USER, ROLE_ADMIN)
        private LocalDateTime created_at; // 계정 생성일
    }

    // 프로필 업데이트 요청 DTO
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateRequest {
        private String nickname;          // 변경할 닉네임
        private Short birth_year;         // 변경할 출생연도
        private String mbti;              // 변경할 MBTI
        private String gender;            // 변경할 성별
        private String current_password;  // 현재 비밀번호 (비밀번호 변경 시 필요)
        private String new_password;      // 새 비밀번호 (비밀번호 변경 시 필요)
    }

    // 회원 등록 요청 DTO
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RegisterRequest {
        private String user_id;           // 로그인 ID
        private String password;          // 비밀번호
        private String nickname;          // 닉네임
        private Short birth_year;         // 출생연도
        private String mbti;              // MBTI 유형
        private String gender;            // 성별
        private String role;              // 역할 (옵션)
    }
}