package com.example.placeservice.service;

import com.example.placeservice.dto.CulturalEventItem;
import com.example.placeservice.dto.CulturalEventResponse;
import com.example.placeservice.entity.CulturalEvent;
import com.example.placeservice.entity.GuArea;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor // final 필드에 대한 생성자 주입
public class EventParserService {

    private final ObjectMapper objectMapper;
    private final GuAreaService guAreaService; // 지역 정보를 가져오는 서비스

    public List<CulturalEventItem> parse(String jsonResponse) {
        try {
            CulturalEventResponse response = objectMapper.readValue(jsonResponse, CulturalEventResponse.class);
            return response.getCulturalEventInfo().getRow();
        } catch (Exception e) {
            log.error("파싱 중 오류 발생: {}", e.getMessage());
            return List.of(); // 빈 리스트 반환
        }
    }

    public CulturalEvent toEntity(CulturalEventItem item) {
        CulturalEvent event = new CulturalEvent();

        // 1. 지역 매핑
        GuArea area = guAreaService.findOrCreateArea(item.getGuname());
        event.setArea(area);

        // 2. 필드 매핑 (컬럼명에 맞춰 정확히 매핑)
        event.setCategory(item.getCodename());           // category
        event.setTitle(item.getTitle());                 // title
        event.setAddress(item.getPlace());             // address
        event.setLat(item.getLat());                     // lat
        event.setLon(item.getLot());                     // lon
        event.setTarget(item.getUseTrgt());               // target
        event.setEventFee(item.getUseFee());                // event_fee
        event.setStartDate(item.getStrtdate());         // start_date
        event.setEndDate(item.getEndDate());             // end_date

        // 3. 이미지 URL 정리
        String imageUrl = item.getMainImg();
        if (imageUrl != null) {
            imageUrl = imageUrl.replace("https://culture.seoul.go.ko?",
                    "https://culture.seoul.go.kr/cmmn/file/getImage.do?");
        }
        event.setEventImg(imageUrl);                               // event_img

        return event;
    }


    public List<CulturalEvent> toEntityList(List<CulturalEventItem> items) {
        return items.stream()
                .map(this::toEntity)
                .toList();
    }
}
