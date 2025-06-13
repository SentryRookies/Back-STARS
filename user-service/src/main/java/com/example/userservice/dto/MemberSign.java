package com.example.userservice.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class MemberSign {
    @JsonProperty("user_id") // JSON 필드와 매핑
    private String userId; // 로그인에 사용할 ID

    private String password; // 비밀번호

    private String nickname; // 닉네임

    @JsonProperty("birth_year") // JSON 필드와 매핑
    private Short birthYear; // 출생연도

    private String mbti; // MBTI 유형

    private String gender; // 성별

    // 관리자 회원가입용 옵션 필드
    private String role; // 사용자 권한 (기본값 ROLE_USER, ROLE_ADMIN 가능)
}