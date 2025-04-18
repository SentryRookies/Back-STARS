package com.example.placeservice.service;

import com.example.placeservice.dto.AttractionDto;
import com.example.placeservice.entity.Area;
import com.example.placeservice.entity.Attraction;
import com.example.placeservice.repository.AreaRepository;
import com.example.placeservice.repository.AttractionRepository;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
                    // visit_id 중복 체크
                    if (attractionRepository.existsByVisitId(table.getId())) return null;

                    // 👇: 조건에 따라 Area 객체를 지정
                    Area area = findAreaByCondition(table, areaList);  // 예: 주소나 지역코드 등으로 판단
                    if (area == null) return null;

                    Attraction attraction = new Attraction();
                    attraction.setVisitId(table.getId());
                    attraction.setName(table.getTitle());
                    attraction.setAddress(table.getNewAddress());
                    attraction.setLat(new BigDecimal(table.getMapX()));
                    attraction.setLon(new BigDecimal(table.getMapY()));
                    attraction.setPhone(table.getTel());
                    attraction.setHomepage_url(table.getHomepage());
                    attraction.setClose_day(table.getCloseDay());
                    attraction.setUse_time(table.getUseTime());

                    attraction.setArea(area);

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
        double lat = Double.parseDouble(table.getMapX());
        double lon = Double.parseDouble(table.getMapY());
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
        // System.out.println(table.getTitle()+"위도 :"+table.getMapX()+"경도 :"+table.getMapY() );
        return null;
    }
}
