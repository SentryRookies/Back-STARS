package com.example.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class MemberSign {
    private Long memberId;
    private String password;
    private String nickName;
    private String createTime;

    public Long getMemberId() {
        return memberId;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickName;
    }
}
