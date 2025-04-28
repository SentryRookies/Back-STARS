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
    @Column(name = "member_id")
    private String memberId;

    @Column(nullable = false)
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "birth_year")
    private Short birthYear;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}