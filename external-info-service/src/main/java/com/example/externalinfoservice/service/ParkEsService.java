package com.example.externalinfoservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParkEsService {

    private final RestTemplate restTemplate = new RestTemplate();

    // 특정 지역 주차장 정보
    public Map<String, Object> getParkFromES(String areaId) {
        String url = "http://elasticsearch.seoultravel.life/seoul_citydata_parking_20250424/_search";
        log.info("Requesting park info for areaId: {}", areaId);

        Map<String, Object> term = Map.of("parking.area_nm", areaId);
        Map<String, Object> query = Map.of("term", term);
        Map<String, Object> body = Map.of("size", 100, "query", query);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        log.info("Request body: {}", body);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        log.info("Response status: {}", response.getStatusCode());

        List<Map<String, Object>> hits = (List<Map<String, Object>>)
                ((Map<String, Object>) response.getBody().get("hits")).get("hits");

        if (hits.isEmpty()) {
            log.warn("No results found for areaId: {}", areaId);
            return null;
        }

        Map<String, Object> source = (Map<String, Object>) hits.get(0).get("_source");
        log.info("Source: {}", source);

        Map<String, Object> parking = (Map<String, Object>) source.get("parking");
        log.info("Parking data: {}", parking);

        return parking;
    }

    // 전체 지역 주차장 정보
    public List<Map<String, Object>> getAllParkFromES() {
        String url = "http://elasticsearch.seoultravel.life/seoul_citydata_parking_20250424/_search";
        log.info("Requesting all park info");

        Map<String, Object> body = Map.of(
                "size", 0,
                "aggs", Map.of(
                        "by_area", Map.of(
                                "terms", Map.of("field", "parking.area_nm", "size", 1000),
                                "aggs", Map.of(
                                        "latest_hit", Map.of(
                                                "top_hits", Map.of(
                                                        "size", 1,
                                                        "sort", List.of(Map.of("_score", Map.of("order", "desc")))
                                                )
                                        )
                                )
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        log.info("Request body for all parks: {}", body);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        log.info("Response status for all parks: {}", response.getStatusCode());
        log.info("Response body keys: {}", response.getBody().keySet());

        if (!response.getBody().containsKey("aggregations")) {
            log.error("Response does not contain 'aggregations' key");
            return Collections.emptyList();
        }

        Map<String, Object> aggregations = (Map<String, Object>) response.getBody().get("aggregations");
        if (!aggregations.containsKey("by_area")) {
            log.error("Aggregations does not contain 'by_area' key");
            return Collections.emptyList();
        }

        Map<String, Object> byArea = (Map<String, Object>) aggregations.get("by_area");
        List<Map<String, Object>> buckets = (List<Map<String, Object>>) byArea.get("buckets");
        log.info("Found {} buckets", buckets.size());

        List<Map<String, Object>> results = new ArrayList<>();
        for (Map<String, Object> bucket : buckets) {
            Map<String, Object> latestHitWrapper = (Map<String, Object>) bucket.get("latest_hit");
            Map<String, Object> hitsWrapper = (Map<String, Object>) latestHitWrapper.get("hits");
            List<Map<String, Object>> hits = (List<Map<String, Object>>) hitsWrapper.get("hits");
            if (hits.isEmpty()) continue;

            Map<String, Object> latestHit = hits.get(0);
            Map<String, Object> source = (Map<String, Object>) latestHit.get("_source");
            Map<String, Object> parkData = (Map<String, Object>) source.get("parking");

            if (parkData != null) {
                results.add(parkData);
                log.info("Added park data for area: {}", parkData.get("area_nm"));
            } else {
                log.warn("Parking data is null for a hit");
            }
        }

        log.info("Returning {} park results", results.size());
        return results;
    }
}