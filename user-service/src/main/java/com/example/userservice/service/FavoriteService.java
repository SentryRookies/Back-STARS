package com.example.userservice.service;

import com.example.userservice.dto.FavoriteDto;
import com.example.userservice.entity.Favorite;
import com.example.userservice.entity.Member;
import com.example.userservice.repository.FavoriteRepository;
import com.example.userservice.repository.jpa.MemberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;
    private static final RestTemplate restTemplate = new RestTemplate();

    @Value("${gateway-url}")
    private String gatewayUrl;

    public List<FavoriteDto> getListData(String userId) {
        try{
            List<Favorite> items = favoriteRepository.findAllByMember_UserId(userId);

            return items.stream()
                    .map(item -> {

                        ResponseEntity<JsonNode> response = restTemplate.exchange(
                                gatewayUrl+"/place/main/info/"+item.getType()+"/" + item.getPlaceId(),
                                HttpMethod.GET,
                                null,
                                JsonNode.class
                        );
                        String name = response.getBody().get(item.getType()+"_name").asText();
                        String address = response.getBody().get("address").asText();
                        return new FavoriteDto(
                            item.getFavoriteId(),
                            item.getType(),
                            item.getPlaceId(),
                            userId,
                            name,
                            address);
                }).toList();

        } catch (Exception e) {
//            throw new RuntimeException(e);
            throw new BadCredentialsException("즐겨찾기 장소 조회 실패",e);
        }
    }

    public ResponseEntity<Map<String, String>> addFavoriteData(String userId, FavoriteDto favoriteDto) {
        Map<String, String> response = new HashMap<>();

        // 1. 사용자 조회
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // 2. 이미 존재하는 즐겨찾기인지 확인
        Optional<Favorite> existingFavorite = favoriteRepository.findByMemberAndTypeAndPlaceId(
                member,
                favoriteDto.getType(),
                favoriteDto.getPlace_id()
        );
        if (existingFavorite.isPresent()) {
            response.put("message", "이미 등록된 즐겨찾기입니다.");
            return ResponseEntity.status(400).body(response);
        }

        // 2. Favorite 엔티티 생성
        Favorite favorite = new Favorite();
        favorite.setType(favoriteDto.getType());
        favorite.setPlaceId(favoriteDto.getPlace_id()); // id는 placeId 역할
        favorite.setMember(member);

        // 3. 저장
        Favorite saved = favoriteRepository.save(favorite);

        response.put("message", "즐겨찾기에 추가되었습니다.");
        return ResponseEntity.ok(response);  // 200 OK와 함께 메세지 전송
    }

    @Transactional
    public ResponseEntity<Map<String, String>> deleteFavorite(String userId, String type, String id) {

        Map<String, String> response = new HashMap<>();

        Member member = memberRepository.findByUserId(userId).orElse(null);
        if (member == null) {
            response.put("message", "사용자 없음");
            return ResponseEntity.status(400).body(response);
        }

        Favorite favorite = favoriteRepository.findByMemberAndTypeAndPlaceId(member, type, id).orElse(null);
        if (favorite == null) {
            response.put("message", "즐겨찾기 없음");
            return ResponseEntity.status(400).body(response);
        }

        favoriteRepository.delete(favorite);
        response.put("message", "즐겨찾기 삭제 완료");
        return ResponseEntity.ok(response);
    }

    public List<Map<String, Object>> getAllFavoriteData() {
        try{
            List<Favorite> items = favoriteRepository.findAll();

            List<FavoriteDto> favoriteDtoList = items.stream()
                    .map(item ->{
                            ResponseEntity<JsonNode> response = restTemplate.exchange(
                                    gatewayUrl+"/place/main/info/"+item.getType()+"/" + item.getPlaceId(),
                                    HttpMethod.GET,
                                    null,
                                    JsonNode.class
                            );
                            String name = response.getBody().get(item.getType()+"_name").asText();
                            String address = response.getBody().get("address").asText();

                            return new FavoriteDto(
                                item.getFavoriteId(),
                                item.getType(),
                                item.getPlaceId(),
                                item.getMember().getUserId(),
                                name,
                                address);
                    }).toList();

            Map<String, List<FavoriteDto>> grouped = favoriteDtoList.stream()
                    .collect(Collectors.groupingBy(FavoriteDto::getUser_id));

            // 원하는 형태로 변환
            List<Map<String, Object>> result = grouped.entrySet().stream()
                    .map(entry -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("user_id", entry.getKey());
                        map.put("content", entry.getValue());
                        return map;
                    })
                    .toList();

            return result;

        } catch (Exception e) {
//            throw new RuntimeException(e);
            throw new BadCredentialsException("즐겨찾기 장소 조회 실패",e);
        }

    }
}
