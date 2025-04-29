package com.example.userservice.repository;

import com.example.userservice.entity.Favorite;
import com.example.userservice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findAllByMember_UserId(String userId);

    Optional<Favorite> findByMemberAndTypeAndPlaceId(Member member, String type, String id);
}
