package com.example.userservice.repository;

import com.example.userservice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByMemberId(Long memberId);
    boolean existsByNickname(String nickname);
}

