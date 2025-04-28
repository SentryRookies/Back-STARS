package com.example.placeservice.service;

import com.example.placeservice.dto.accommodation.AccommodationDto;
import com.example.placeservice.dto.accommodation.KakaoApiResponse;
import com.example.placeservice.entity.Accommodation;
import com.example.placeservice.entity.Area;
import com.example.placeservice.repository.AccommodationRepository;
import com.example.placeservice.repository.AreaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccommodationService {
    private final AreaRepository areaRepository;
    private final AccommodationRepository accommodationRepository; // AccommodationRepository 주입
    private final ObjectMapper objectMapper;

    private final String baseUrl = "https://dapi.kakao.com/v2/local/search/category.json";
    private final RestTemplate restTemplate;
    private final Dotenv dotenv;

    public AccommodationService(AreaRepository areaRepository, AccommodationRepository accommodationRepository, ObjectMapper objectMapper, RestTemplate restTemplate, Dotenv dotenv) {
        this.areaRepository = areaRepository;
        this.accommodationRepository = accommodationRepository;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.dotenv = dotenv;
    }

    public String searchAccommodations(BigDecimal x, BigDecimal y) {
        HttpHeaders headers = new HttpHeaders();
        String kakaoApiKey = dotenv.get("KAKAO_API_KEY");
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("category_group_code", "AD5")
                .queryParam("radius", 1000)
                .queryParam("x", x.toString()) // BigDecimal을 문자열로 변환
                .queryParam("y", y.toString()) // BigDecimal을 문자열로 변환
                .build()
                .encode(StandardCharsets.UTF_8) // UTF-8 인코딩 명시
                .toUri();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    String.class // 응답 본문을 문자열(JSON)로 받음
            );

            // 응답 상태 코드가 OK(200) 인지 확인
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody(); // 성공 시 응답 본문(JSON 문자열) 반환 [1]
            } else {
                // API 호출은 성공했으나, 응답 코드가 200 OK가 아닌 경우
                System.out.println("Error calling Kakao API. Status code: " + response.getStatusCode() + " Response: " + response.getBody());
                return null; // 실패 시 null 반환 [1]
            }
        } catch (RestClientException e) {
            // RestTemplate 사용 중 네트워크 오류 등 예외 발생 시
            System.out.println("Exception during Kakao Api call to " + uri + ": " + e.getMessage() + e);
            return null; // 예외 발생 시 null 반환
        }
    }

    @Transactional
    public void saveAccommodations() throws JsonProcessingException {
        List<Area> areas = areaRepository.findAll();

        List<Accommodation> accommodationsToSave = new ArrayList<>();

        for (Area area : areas) {
            BigDecimal lon = area.getLon(); // Area에서 경도(lon) 가져오기
            BigDecimal lat = area.getLat(); // Area에서 위도(lat) 가져오기

            String jsonResponse = searchAccommodations(lon, lat); // API 호출 함수 분리

            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                try {
                    KakaoApiResponse apiResponse = objectMapper.readValue(jsonResponse, KakaoApiResponse.class);

                    if (apiResponse != null && apiResponse.getDocuments() != null) {
                        for (KakaoApiResponse.Document doc : apiResponse.getDocuments()) {
                            Long kakaoId = Long.parseLong(doc.getId());

                            if (!accommodationRepository.existsByAccommodationId(kakaoId)) { // 중복 여부 확인
                                Accommodation accommodation = mapDocumentToAccommodation(doc, area); // 매핑 함수 사용
                                accommodationsToSave.add(accommodation);

                            } else {
                                System.out.println("새롭게 저장할 숙박업소가 없습니다.");
                            }
                        }
                    }
                } catch (JsonProcessingException e) {
                    System.out.println("Json 파싱 실패");
                } catch (NumberFormatException e) {
                    System.out.println("숙박업소 Id 오류");
                }
            } else {
                System.out.println("유효한 응답이 없습니다.");
            }
            // try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        // 5. 수집된 숙소 정보 일괄 저장
        if (!accommodationsToSave.isEmpty()) {
            accommodationRepository.saveAll(accommodationsToSave); // saveAll로 성능 향상
            System.out.println("저장 성공");
        } else {
            System.out.println("신규 저장 숙박업소가 없습니다.");
        }
    }

    private Accommodation mapDocumentToAccommodation(KakaoApiResponse.Document doc, Area area) {
        Accommodation accommodation = new Accommodation();
        try {
            accommodation.setAccommodationId(Long.parseLong(doc.getId())); // String ID를 Long으로 변환
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid accommodation ID format: " + doc.getId(), e);
        }
        accommodation.setArea(area);
        accommodation.setName(doc.getPlaceName());
        accommodation.setAddress(doc.getRoadAddressName());
        try {
            accommodation.setLat(new BigDecimal(doc.getY())); // String 좌표를 BigDecimal로 변환
            accommodation.setLon(new BigDecimal(doc.getX())); // String 좌표를 BigDecimal로 변환
        } catch (NumberFormatException e) {
            System.out.println("좌표 설정 실패");
            accommodation.setLat(null);
            accommodation.setLon(null);
        }
        accommodation.setPhone(doc.getPhone());

        String[] parts = doc.getRoadAddressName().split(" ");
        for (String part : parts) {
            if (part.endsWith("구")) {
                accommodation.setGu(part);
            }
        }

        parts = doc.getCategoryName().split(" > ");
        if (parts.length > 2) {
            accommodation.setType(parts[2]);
        }
        else {
            accommodation.setType("구분없음");
        }
        accommodation.setKakaomapUrl(doc.getPlaceUrl());

        return accommodation;
    }

    public List<AccommodationDto> showAccommodations() {
        List<Accommodation> accommodations = accommodationRepository.findAll();
        List<AccommodationDto> accommodationDtos = new ArrayList<>();
        for (Accommodation accommodation : accommodations) {
            AccommodationDto accommodationDto = AccommodationDto.fromEntity(accommodation);
            accommodationDtos.add(accommodationDto);
        }
        return accommodationDtos;
    }

    public AccommodationDto getAccommodationById(Long id) {
        return AccommodationDto.fromEntity(accommodationRepository.findById(id).get());
    }

    public List<AccommodationDto> getAccommodationByGu(String gu) {
        List<Accommodation> accommodations = accommodationRepository.findByGu(gu);
        List<AccommodationDto> accommodationDtos = new ArrayList<>();
        for (Accommodation accommodation : accommodations) {
            AccommodationDto accommodationDto = AccommodationDto.fromEntity(accommodation);
            accommodationDtos.add(accommodationDto);
        }
        return accommodationDtos;
    }

    public List<AccommodationDto> getAccommodationByType(String type) {
        List<Accommodation> accommodations = accommodationRepository.findByType(type);
        List<AccommodationDto> accommodationDtos = new ArrayList<>();
        for (Accommodation accommodation : accommodations) {
            AccommodationDto accommodationDto = AccommodationDto.fromEntity(accommodation);
            accommodationDtos.add(accommodationDto);
        }
        return accommodationDtos;
    }
}