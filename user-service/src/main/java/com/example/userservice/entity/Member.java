package com.example.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long memberId; // 회원 고유 ID (시스템 내부용)

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId; // 사용자 로그인 ID

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column
    private String nickname; // 닉네임

    @Column(name = "birth_year")
    private Short birthYear; // 출생연도

    @Column(name = "mbti")
    private String mbti; // MBTI 유형

    @Column(name = "gender")
    private String gender; // 성별

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 계정 생성일

    @Column(name = "role", nullable = false)
    private String role; // 사용자 권한 ("ROLE_ADMIN" 또는 "ROLE_USER")
}