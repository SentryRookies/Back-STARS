package com.example.placeservice.service;

import com.example.placeservice.dto.CulturalEventItem;
import com.example.placeservice.entity.CulturalEvent;
import com.example.placeservice.external.CulturalEventClient;
import com.example.placeservice.repository.CulturalEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CulturalEventService {

    private final CulturalEventClient culturalEventClient;
    private final EventParserService eventParserService;
    private final CulturalEventRepository culturalEventRepository;

    // API에서 데이터를 1000개씩 받아와서 저장하는 메서드
    @Transactional
    public void fetchAndSaveAllEvents() {
        int[] ranges = {1, 1001, 2001, 3001, 4001, 5001}; // 각 범위의 시작 인덱스
        for (int i = 0; i < ranges.length; i++) {
            int startIndex = ranges[i];
            int endIndex = startIndex+999;  // endIndex는 1000개씩 데이터를 가져오므로 1000개씩 증가

            // API에서 데이터 가져오기
            String jsonData = culturalEventClient.fetchEventData(startIndex, endIndex);
            List<CulturalEventItem> eventItems = eventParserService.parse(jsonData);

            // 데이터가 없다면 더 이상 가져올 데이터가 없다는 의미
            if (!eventItems.isEmpty()) {
                // 중복 제거하고 저장
                saveEvents(eventItems);
            }
        }
    }

    private void saveEvents(List<CulturalEventItem> eventItems) {
        List<CulturalEvent> events = eventParserService.toEntityList(eventItems);
        for (CulturalEvent event : events) {
            // 중복 처리 로직
            if (!culturalEventRepository.existsByTitleAndAddressAndStartDate(event.getTitle(), event.getAddress(), event.getStartDate())) {
                culturalEventRepository.save(event);
            }
        }
    }
}
