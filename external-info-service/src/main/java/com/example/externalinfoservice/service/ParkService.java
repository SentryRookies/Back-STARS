package com.example.externalinfoservice.service;

import com.example.externalinfoservice.dto.ParkDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ParkService {

    @Value("${seoul.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public ParkService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> getParkingData(String areaId) {
        try {
            String url = String.format(
                    "http://openapi.seoul.go.kr:8088/%s/xml/citydata/1/5/%s",
                    apiKey, areaId
            );

            String xmlResponse = restTemplate.getForObject(url, String.class);

            XmlMapper xmlMapper = new XmlMapper();
            JsonNode root = xmlMapper.readTree(xmlResponse);

            // 실제 PRK_STTS 배열에 접근
            JsonNode prkArray = root
                    .path("CITYDATA")
                    .path("PRK_STTS")
                    .path("PRK_STTS");

            if (!prkArray.isArray()) {
                throw new RuntimeException("PRK_STTS는 배열이 아닙니다.");
            }

            // 제거할 필드들: LNG, LAT, ADDRESS, ROAD_ADDR
            for (JsonNode node : prkArray) {
                if (node.isObject()) {
                    ((com.fasterxml.jackson.databind.node.ObjectNode) node).remove("LNG");
                    ((com.fasterxml.jackson.databind.node.ObjectNode) node).remove("LAT");
                    ((com.fasterxml.jackson.databind.node.ObjectNode) node).remove("ADDRESS");
                    ((com.fasterxml.jackson.databind.node.ObjectNode) node).remove("ROAD_ADDR");
                }
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Object prkListJson = objectMapper.treeToValue(prkArray, Object.class);

            return Map.of("data", Map.of("PRK_STTS", prkListJson));

        } catch (Exception e) {
            throw new RuntimeException("주차장 데이터 파싱 실패: " + e.getMessage(), e);
        }
    }
}
