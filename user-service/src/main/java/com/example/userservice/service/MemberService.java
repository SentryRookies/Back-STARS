package com.example.userservice.service;

import com.example.userservice.dto.MemberSign;
import com.example.userservice.entity.Member;
import com.example.userservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Member registerAdmin(MemberSign dto) {
        if (memberRepository.existsByMemberId(dto.getMemberId())) {
            throw new IllegalStateException("이미 존재하는 ID입니다.");
        }
        if (memberRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        }

        Member member = new Member();
        member.setPassword(passwordEncoder.encode(dto.getPassword()));
        member.setNickname(dto.getNickname());
        member.setCreateAt(String.valueOf(LocalDateTime.now()));

        Member savedMember = memberRepository.save(member);
        return savedMember; // 저장된 객체 리턴
    }
}
