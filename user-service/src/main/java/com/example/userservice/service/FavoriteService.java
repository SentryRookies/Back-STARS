package com.example.userservice.service;

import com.example.userservice.dto.FavoriteDto;
import com.example.userservice.entity.Favorite;
import com.example.userservice.entity.Member;
import com.example.userservice.entity.place.Accommodation;
import com.example.userservice.entity.place.Attraction;
import com.example.userservice.entity.place.Cafe;
import com.example.userservice.entity.place.Restaurant;
import com.example.userservice.repository.FavoriteRepository;
import com.example.userservice.repository.jpa.MemberRepository;
import com.example.userservice.repository.place.AccommodationRepository;
import com.example.userservice.repository.place.AttractionRepository;
import com.example.userservice.repository.place.CafeRepository;
import com.example.userservice.repository.place.RestaurantRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;
    private static final RestTemplate restTemplate = new RestTemplate();

    private final AccommodationRepository accommodationRepository;
    private final AttractionRepository attractionRepository;
    private final CafeRepository cafeRepository;
    private final RestaurantRepository restaurantRepository;

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
                    .map(item ->{

                            if(Objects.equals(item.getType(), "cafe")){
                                Optional<Cafe> cafe = cafeRepository.findById(Long.valueOf(item.getPlaceId()));
                                String name = cafe.get().getName();
                                String address = cafe.get().getAddress();
                                return new FavoriteDto(
                                        item.getFavoriteId(),
                                        item.getType(),
                                        item.getPlaceId(),
                                        item.getMember().getUserId(),
                                        name,
                                        address);
                            }else if(Objects.equals(item.getType(), "restaurant")){
                                Optional<Restaurant> retaurant = restaurantRepository.findById(Long.valueOf(item.getPlaceId()));
                                String name = retaurant.get().getName();
                                String address = retaurant.get().getAddress();
                                return new FavoriteDto(
                                        item.getFavoriteId(),
                                        item.getType(),
                                        item.getPlaceId(),
                                        item.getMember().getUserId(),
                                        name,
                                        address);
                            }else if(Objects.equals(item.getType(), "attraction")){
                                Optional<Attraction> attraction = attractionRepository.findById(Long.valueOf(item.getPlaceId()));
                                String name = attraction.get().getName();
                                String address = attraction.get().getAddress();
                                return new FavoriteDto(
                                        item.getFavoriteId(),
                                        item.getType(),
                                        item.getPlaceId(),
                                        item.getMember().getUserId(),
                                        name,
                                        address);
                            }else if(Objects.equals(item.getType(), "accommodation")){
                                Optional<Accommodation> accommodation = accommodationRepository.findById(Long.valueOf(item.getPlaceId()));
                                String name = accommodation.get().getName();
                                String address = accommodation.get().getAddress();
                                return new FavoriteDto(
                                        item.getFavoriteId(),
                                        item.getType(),
                                        item.getPlaceId(),
                                        item.getMember().getUserId(),
                                        name,
                                        address);
                            }


//                            ResponseEntity<JsonNode> response = restTemplate.exchange(
//                                    gatewayUrl+"/place/main/info/"+item.getType()+"/" + item.getPlaceId(),
//                                    HttpMethod.GET,
//                                    null,
//                                    JsonNode.class
//                            );
//                            String name = response.getBody().get(item.getType()+"_name").asText();
//                            String address = response.getBody().get("address").asText();

                        return null;
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
            log.error(e.getMessage());
            throw new BadCredentialsException("즐겨찾기 장소 조회 실패",e);
        }

    }
}
