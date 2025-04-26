package com.example.userservice.service;

import com.example.userservice.dto.MemberSign;
import com.example.userservice.entity.Member;
import com.example.userservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public void registerAdmin(MemberSign dto) {
        if(memberRepository.existsByMemberId(Long.valueOf(String.valueOf(dto.getMemberId())))){
            throw new IllegalStateException("이미 존재하는 ID입니다.");
        }
        Member member = new Member();
        member.setMemberId(dto.getMemberId());
        member.setPassword(dto.getPassword());
        member.setNickname(dto.getNickname());
        member.setCreateAt(String.valueOf(LocalDateTime.now()));

        memberRepository.save(member);
    }

}
