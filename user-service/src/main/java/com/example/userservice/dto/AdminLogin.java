package com.example.userservice.dto;

import lombok.Data;

@Data
public class AdminLogin {
    private Long memberId;
    private String password;
}
