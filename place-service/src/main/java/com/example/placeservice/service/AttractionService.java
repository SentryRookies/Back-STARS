package com.example.placeservice.service;

import com.example.placeservice.dto.attraction.AreaAttractionsDto;
import com.example.placeservice.dto.attraction.AttractionDto;
import com.example.placeservice.dto.attraction.AttractionInfoDto;
import com.example.placeservice.dto.attraction.AttractionListDto;
import com.example.placeservice.entity.Area;
import com.example.placeservice.entity.Attraction;
import com.example.placeservice.repository.AreaRepository;
import com.example.placeservice.repository.AttractionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static com.example.placeservice.util.GeoUtils.calculateDistanceKm;

@Service
@RequiredArgsConstructor
public class AttractionService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final AttractionRepository attractionRepository;
    private final AreaRepository areaRepository;
    private final AttractionKakaoMapClient attractionKakaoMapClient;

    // visitSeoul 관광지 데이터 로드 후 attraction 테이블 저장
    public List<Attraction> fetchDataFromVisitSeoul() {
        String url="https://www.visitseoul.net/file_save/OPENAPI/OPEN_API_kr.xml";
        List<Area> areaList = areaRepository.findAll(); // 여기서 불러와

        try {
            // 1. XML 받아오기
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String xml = new String(response.getBody().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            xml = xml.trim().replace("\uFEFF", "");

            // 2. JAXB로 XML → DTO 변환
            JAXBContext jaxbContext = JAXBContext.newInstance(AttractionDto.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            AttractionDto dto = (AttractionDto) unmarshaller.unmarshal(reader);

            // 3. DTO → Entity로 변환 (매핑)
            List<Attraction> entities = dto.getAttractions().stream()
                .map(table -> {

                    // seoul_attraction_id 중복 체크
                    if (attractionRepository.existsBySeoulAttractionId(table.getId())) return null;

                    // 👇: 조건에 따라 Area 객체를 지정
                    Area area = findAreaByCondition(table, areaList);  // 예: 주소나 지역코드 등으로 판단
                    if (area == null) return null;


                    Attraction attraction = new Attraction();
                    attraction.setSeoulAttractionId(table.getId());
                    attraction.setName(table.getTitle());
                    attraction.setAddress(table.getNewAddress());
                    attraction.setLat(new BigDecimal(table.getMapY()));
                    attraction.setLon(new BigDecimal(table.getMapX()));
                    attraction.setPhone(table.getTel());
                    attraction.setHomepageUrl(table.getHomepage());
                    attraction.setCloseDay(table.getCloseDay());
                    attraction.setUseTime(table.getUseTime());
                    attraction.setArea(area);

                    // kakaomap_url 추가
                    String title = table.getTitle();
                    if (title.length() <= 100){ // 키워드 최대 100글자까지 검색가능
                        JsonNode kakao_response = attractionKakaoMapClient.searchAttractionByKeyword(title);
                        if (kakao_response.has("documents")) {
                            for (JsonNode doc : kakao_response.get("documents")) {
                                String categoryName = doc.get("category_name").asText();
                                if (categoryName.contains("여행 > 관광") || categoryName.contains("여행 > 명소")) {
//                                    System.out.println("🎯 관광 관련 장소: " +table.getTitle()+"는 바로바로 :::"+ doc.get("place_name").asText());
                                    attraction.setKakaomapUrl(doc.get("place_url").asText());
                                    break;
                                }
                            }
                        }else{System.out.println("카카오 관광지 검색 결과가 없습니다");}
                    }else{System.out.println(table.getTitle());}


                return attraction;
            })
            .filter(Objects::nonNull) // null 필터링
            .toList();

            // 4. DB에 저장
            return attractionRepository.saveAll(entities);

        } catch (Exception e) {
            e.printStackTrace(); // 로깅 처리 추천
            throw new RuntimeException("외부 XML 데이터 파싱 실패", e);
        }
    }

    // 관광지 2km이내 area 계산
    private Area findAreaByCondition(AttractionDto.AttractionTable table, List<Area> areaList) {
        double lat = Double.parseDouble(table.getMapY());
        double lon = Double.parseDouble(table.getMapX());
        for (Area area : areaList) {
            double distance = calculateDistanceKm(
                    lat, lon,
                    area.getLat().doubleValue(), area.getLon().doubleValue()
            );
            if (distance <= 2.0) { // 2km 이내
                return area;
            }
        }
        // 2km 이내에 없는 관광지 체크
        // System.out.println(table.getTitle()+"경도:"+table.getMapX()+"위도 :"+table.getMapY() );
        return null;
    }




    // attraction 목록 DB 불러오기
    public List<AreaAttractionsDto> getAttractionData() {
        try {
                List<Area> areas = areaRepository.findAll();

                return areas.stream()
                        .map(area -> {
                            List<AttractionListDto> attractions = area.getAttractions().stream()
                                    .map(attraction -> new AttractionListDto(
                                            attraction.getAttractionId(),
                                            attraction.getSeoulAttractionId(),
                                            attraction.getName(),
                                            attraction.getAddress(),
                                            attraction.getLat(),
                                            attraction.getLon(),
                                            attraction.getKakaomapUrl()
                                    )).toList();

                            AreaAttractionsDto dto = new AreaAttractionsDto();
                            dto.setArea_name(area.getName());
                            dto.setAttraction_list(attractions);
                            return dto;
                        })
                        .toList();

        } catch (RuntimeException e) {
            throw new RuntimeException("예상치 못한 오류",e);
        }
    }

    public AttractionInfoDto getAttractionInfoData(long attractionId) {
        try{
            Attraction attraction = attractionRepository.findByAttractionId(attractionId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 관광지 정보를 찾을 수 없습니다."));

            return new AttractionInfoDto(
                    attraction.getAttractionId(),
                    attraction.getSeoulAttractionId(),
                    attraction.getName(),
                    attraction.getAddress(),
                    attraction.getLat(),
                    attraction.getLon(),
                    attraction.getPhone(),
                    attraction.getHomepageUrl(),
                    attraction.getCloseDay(),
                    attraction.getUseTime(),
                    attraction.getKakaomapUrl()
            );

        } catch (RuntimeException e) {
            throw new RuntimeException("예상치 못한 오류",e);
        }
    }
}
