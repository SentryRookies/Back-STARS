package com.example.userservice.service;

import com.example.userservice.dto.MemberSign;
import com.example.userservice.entity.Member;
import com.example.userservice.repository.jpa.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member registerAdmin(MemberSign dto) {
        // userId 중복 체크
        if (memberRepository.existsByUserId(dto.getUserId())) {
            throw new IllegalStateException("이미 존재하는 사용자 ID입니다.");
        }

        if (memberRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        }

        Member member = Member.builder()
                .userId(dto.getUserId())  // userId 설정
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .birthYear(dto.getBirthYear())
                .createdAt(LocalDateTime.now())
                .build();

        return memberRepository.save(member);
    }
}