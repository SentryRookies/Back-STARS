package com.example.userservice.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class MemberSign {
    @JsonProperty("user_id")  // JSON 필드 이름 매핑
    private String userId;

    private String password;
    private String nickname;
    private String createTime;

    @JsonProperty("birth_year")  // JSON 필드 이름 매핑
    private Short birthYear;
}