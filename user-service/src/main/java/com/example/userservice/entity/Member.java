package com.example.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor // JPA용 기본 생성자
@AllArgsConstructor // 모든 필드로 객체를 편하게 생성
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id",nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String password;

    @Column
    private String nickname;

    @Column(name = "create_at")
    private String createAt;


}
