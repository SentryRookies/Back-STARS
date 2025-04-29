package com.example.userservice.service;

import com.example.userservice.dto.FavoriteDto;
import com.example.userservice.entity.Favorite;
import com.example.userservice.entity.Member;
import com.example.userservice.repository.FavoriteRepository;
import com.example.userservice.repository.jpa.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;

    public List<FavoriteDto> getListData(String userId) {
        try{
            List<Favorite> items = favoriteRepository.findAllByMember_UserId(userId);
            return items.stream()
                    .map(item -> new FavoriteDto(
                            item.getFavoriteId(),
                            item.getType(),
                            item.getPlaceId(),
                            userId
                    )).toList();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public FavoriteDto addFavoriteData(String userId, FavoriteDto favoriteDto) {

        // 1. 사용자 조회
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // 2. Favorite 엔티티 생성
        Favorite favorite = new Favorite();
        favorite.setType(favoriteDto.getType());
        favorite.setPlaceId(favoriteDto.getPlace_id()); // id는 placeId 역할
        favorite.setMember(member);

        // 3. 저장
        Favorite saved = favoriteRepository.save(favorite);

        // 4. DTO로 변환 후 반환
        FavoriteDto result = new FavoriteDto(saved.getFavoriteId(),member.getUserId(), saved.getType(), saved.getPlaceId());

        return result;
    }

    @Transactional
    public void deleteFavorite(String userId, String type, String id) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Favorite favorite = favoriteRepository.findByMemberAndTypeAndPlaceId(member, type, id)
                .orElseThrow(() -> new RuntimeException("즐겨찾기 없음"));

        favoriteRepository.delete(favorite);
    }

    public List<FavoriteDto> getAllFavoriteData() {
        try{
            List<Favorite> items = favoriteRepository.findAll();
            return items.stream()
                    .map(item -> new FavoriteDto(
                            item.getFavoriteId(),
                            item.getType(),
                            item.getPlaceId(),
                            item.getMember().getUserId()
                    )).toList();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
