package com.example.placeservice.service;

import com.example.placeservice.dto.CulturalEventItem;
import com.example.placeservice.entity.Area;
import com.example.placeservice.entity.CulturalEvent;
import com.example.placeservice.external.CulturalEventClient;
import com.example.placeservice.repository.AreaRepository;
import com.example.placeservice.repository.CulturalEventRepository;
import com.example.placeservice.util.GeoUtils;
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
    private final AreaRepository areaRepository; // 🔹 추가

    @Transactional
    public void fetchAndSaveAllEvents() {
        int[] ranges = {1, 1001, 2001, 3001, 4001, 5001};
        for (int i = 0; i < ranges.length; i++) {
            int startIndex = ranges[i];
            int endIndex = startIndex + 999;

            String jsonData = culturalEventClient.fetchEventData(startIndex, endIndex);
            List<CulturalEventItem> eventItems = eventParserService.parse(jsonData);

            if (!eventItems.isEmpty()) {
                saveEvents(eventItems);
            }
        }
    }

    private void saveEvents(List<CulturalEventItem> eventItems) {
        List<CulturalEvent> events = eventParserService.toEntityList(eventItems);
        List<Area> areas = areaRepository.findAll(); // 🔹 모든 지역 좌표 불러오기

        for (CulturalEvent event : events) {
            if (!culturalEventRepository.existsByTitleAndAddressAndStartDate(
                    event.getTitle(), event.getAddress(), event.getStartDate())) {

                // 🔹 행사 위치 기준 가장 가까운 지역 찾기
                Area nearestArea = findNearestArea(event.getLat().doubleValue(), event.getLon().doubleValue(), areas);
                event.setArea(nearestArea); // 🔹 지역 설정

                culturalEventRepository.save(event);
            }
        }
    }

    private Area findNearestArea(double lat, double lon, List<Area> areas) {
        double minDistance = Double.MAX_VALUE;
        Area nearestArea = null;

        for (Area area : areas) {
            double areaLat = area.getLat().doubleValue();
            double areaLon = area.getLon().doubleValue();
            double distance = GeoUtils.calculateDistanceKm(lat, lon, areaLat, areaLon);

            if (distance < minDistance) {
                minDistance = distance;
                nearestArea = area;
            }
        }

        return nearestArea;
    }
}
