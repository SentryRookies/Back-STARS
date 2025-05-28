package com.example.userservice.service;

import com.example.userservice.dto.FavoriteDto;
import com.example.userservice.entity.Favorite;
import com.example.userservice.entity.Member;
import com.example.userservice.repository.FavoriteRepository;
import com.example.userservice.repository.jpa.MemberRepository;
import com.example.userservice.repository.place.AccommodationRepository;
import com.example.userservice.repository.place.AttractionRepository;
import com.example.userservice.repository.place.CafeRepository;
import com.example.userservice.repository.place.RestaurantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;

    private final AccommodationRepository accommodationRepository;
    private final AttractionRepository attractionRepository;
    private final CafeRepository cafeRepository;
    private final RestaurantRepository restaurantRepository;

//    @Value("${gateway-url}")
//    private String gatewayUrl;

    public FavoriteDto FindPlace(Favorite item){
        Long placeId = Long.valueOf(item.getPlaceId());
        String type = item.getType();
        return switch (type) {
            case "cafe" -> cafeRepository.findById(placeId)
                    .map(cafe -> buildFavoriteDto(item, cafe.getName(), cafe.getAddress()))
                    .orElseThrow(() -> new NoSuchElementException("Cafe not found"));
            case "restaurant" -> restaurantRepository.findById(placeId)
                    .map(restaurant -> buildFavoriteDto(item, restaurant.getName(), restaurant.getAddress()))
                    .orElseThrow(() -> new NoSuchElementException("Restaurant not found"));
            case "attraction" -> attractionRepository.findById(placeId)
                    .map(attraction -> buildFavoriteDto(item, attraction.getName(), attraction.getAddress()))
                    .orElseThrow(() -> new NoSuchElementException("Attraction not found"));
            case "accommodation" -> accommodationRepository.findById(placeId)
                    .map(acc -> buildFavoriteDto(item, acc.getName(), acc.getAddress()))
                    .orElseThrow(() -> new NoSuchElementException("Accommodation not found"));
            default -> throw new IllegalArgumentException("장소 type 오류: " + type);
        };
    }

    private FavoriteDto buildFavoriteDto(Favorite item, String name, String address) {
        return new FavoriteDto(
                item.getFavoriteId(),
                item.getType(),
                item.getPlaceId(),
                item.getMember().getUserId(),
                name,
                address
        );
    }

    public List<FavoriteDto> getListData(String userId) {
        try{
            List<Favorite> items = favoriteRepository.findAllByMember_UserId(userId);

            return items.stream()
                    .map(this::FindPlace).toList();

        } catch (Exception e) {
            log.error(e.getMessage());
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

        // 3. 존재하는 장소인지 type/id 확인
        if(Objects.equals(favoriteDto.getType(), "cafe")){
            if(!cafeRepository.existsById(Long.valueOf(favoriteDto.getPlace_id()))){
                response.put("message", "잘못된 장소 type/id 입니다 ");
                return ResponseEntity.status(400).body(response);
            }
        }else if(Objects.equals(favoriteDto.getType(), "restaurant")){
            if(!restaurantRepository.existsById(Long.valueOf(favoriteDto.getPlace_id()))){
                response.put("message", "잘못된 장소 type/id 입니다 ");
                return ResponseEntity.status(400).body(response);
            }
        }else if(Objects.equals(favoriteDto.getType(), "attraction")){
            if(!attractionRepository.existsById(Long.valueOf(favoriteDto.getPlace_id()))){
                response.put("message", "잘못된 장소 type/id 입니다 ");
                return ResponseEntity.status(400).body(response);
            }
        }else if(Objects.equals(favoriteDto.getType(), "accommodation")){
            if(!accommodationRepository.existsById(Long.valueOf(favoriteDto.getPlace_id()))){
                response.put("message", "잘못된 장소 type/id 입니다 ");
                return ResponseEntity.status(400).body(response);
            }
        }

//        //place-service와 REST 통신 방식
//        ResponseEntity<JsonNode> checkPlace = restTemplate.exchange(
//                gatewayUrl + "/place/main/info/" + favoriteDto.getType() + "/" + favoriteDto.getPlace_id(),
//                HttpMethod.GET,
//                null,
//                JsonNode.class
//        );
//

        // 4. Favorite 엔티티 생성
        Favorite favorite = new Favorite();
        favorite.setType(favoriteDto.getType());
        favorite.setPlaceId(favoriteDto.getPlace_id()); // id는 placeId 역할
        favorite.setMember(member);

        // 5. 저장
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
                    .map(this::FindPlace).toList();

            Map<String, List<FavoriteDto>> grouped = favoriteDtoList.stream()
                    .collect(Collectors.groupingBy(FavoriteDto::getUser_id));

            // 원하는 형태로 변환

            return grouped.entrySet().stream()
                    .map(entry -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("user_id", entry.getKey());
                        map.put("content", entry.getValue());
                        return map;
                    })
                    .toList();

        } catch (Exception e) {
//            throw new RuntimeException(e);
            log.error(e.getMessage());
            throw new BadCredentialsException("즐겨찾기 장소 조회 실패",e);
        }

    }

}
