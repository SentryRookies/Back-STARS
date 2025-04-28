package com.example.userservice.repository;

import com.example.userservice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByMemberId(Long memberId);
    boolean existsByUserId(String userId);
    boolean existsByNickname(String nickname);
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByUserId(String userId);
}