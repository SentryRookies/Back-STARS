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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static com.example.placeservice.util.GeoUtils.calculateDistanceKm;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttractionService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final AttractionRepository attractionRepository;
    private final AreaRepository areaRepository;
    private final AttractionKakaoMapClient attractionKakaoMapClient;

    // visitSeoul 관광지 데이터 로드 후 attraction 테이블 저장
    public void fetchDataFromVisitSeoul() {
        String url="https://www.visitseoul.net/file_save/OPENAPI/OPEN_API_kr.xml";
        List<Area> areaList = areaRepository.findAll();

        log.info("관광지 데이터 저장 시작");

        try {
            // 1. XML 받아오기
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String xml = new String(Objects.requireNonNull(response.getBody()).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
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
                    if (Integer.parseInt(table.getId()) >= 31345 && Integer.parseInt(table.getId()) <= 53595) return null;

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


                    if(normalizeAndValidateUrl(table.getHomepage()) != null){
                        attraction.setHomepageUrl(normalizeAndValidateUrl(table.getHomepage()));
                    }

//                    attraction.setHomepageUrl(table.getHomepage());
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
                        }else{
                            log.debug("카카오 관광지 검색 결과가 없습니다 : {}", title);}
                    }
                return attraction;
            })
            .filter(Objects::nonNull) // null 필터링
            .toList();

            log.info("관광지 데이터 저장 완료 : {}",entities.size());
            // 4. DB에 저장
            attractionRepository.saveAll(entities);

        } catch (Exception e) {
            log.error("관광지 데이터 저장 중 오류 발생 : {}",e.getMessage());
            throw new RuntimeException("외부 XML 데이터 파싱 실패", e);
        }
    }

    private Area findAreaByCondition(AttractionDto.AttractionTable table, List<Area> areaList) {
        double lat = Double.parseDouble(table.getMapY());
        double lon = Double.parseDouble(table.getMapX());

        Area closestArea = null;
        double minDistance = Double.MAX_VALUE;

        for (Area area : areaList) {
            double distance = calculateDistanceKm(
                    lat, lon,
                    area.getLat().doubleValue(), area.getLon().doubleValue()
            );

            if (distance < minDistance) {
                minDistance = distance;
                closestArea = area;
            }
        }
        if (minDistance > 2.0) { // 2. 거리가 2km를 넘어가면 제외한다.
            log.debug("{} - 가까운 지역 없음 (거리: {}km)", table.getTitle(), minDistance);
            return null;
        }
        return closestArea;
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
            log.error("관광지 목록 조회 오류 발생 : {}", e.getMessage());
            throw new RuntimeException("예상치 못한 오류",e);
        }
    }

    public AttractionInfoDto getAttractionInfoData(long attractionId) {
        try{
            Attraction attraction = attractionRepository.findByAttractionId(attractionId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 관광지 정보를 찾을 수 없습니다."));

            // 연결된 Area 정보 가져오기
            Area area = attraction.getArea(); // 여기에서 바로 가져올 수 있음
            String areaName = (area != null) ? area.getName() : null;
            Long areaId = (area != null) ? area.getAreaId() : null;

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
                    attraction.getKakaomapUrl(),
                    areaId,
                    areaName
            );

        } catch (RuntimeException e) {
            log.error("관광지 정보 조회 오류 발생 : {}", e.getMessage());
            throw new RuntimeException("예상치 못한 오류",e);
        }
    }

    // 접속이 가능한 홈페이지인지 확인
    public String normalizeAndValidateUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) return null;

        // 기본적인 정제
        String url = rawUrl.trim();
        if (!url.startsWith("http")) {
            url = "https://" + url;
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            int code = conn.getResponseCode();

            if (code >= 200 && code < 400) {
                return url;
            }else{
                return null;
            }
        } catch (Exception e) {
            return null; // 실패 시 null 반환
        }

    }
}
