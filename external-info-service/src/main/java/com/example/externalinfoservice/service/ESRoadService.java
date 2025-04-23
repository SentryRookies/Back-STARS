package com.example.externalinfoservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ESRoadService {

    private static final RestTemplate restTemplate = new RestTemplate();

    public static JsonNode getTrafficData(String areaNm) {
        try {
            String apiUrl = "http://elasticsearch.seoultravel.life/seoul_citydata_road/_search"; // 외부 API URL

            // JSON Body 생성
            String jsonBody = String.format("{\n" +
                    "  \"size\": 1,\n" +
                    "  \"query\": {\n" +
                    "    \"term\": {\n" +
                    "      \"road_traffic.area_nm.keyword\": \"%s\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}", areaNm);
            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // 요청 엔티티 만들기
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            // POST 요청 보내기
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

            String body = response.getBody();
            // road_traffic 데이터만 가져오기
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(String.valueOf(body)); // response는 JSON 문자열

            JsonNode roadTraffic = root
                    .path("hits")
                    .path("hits")
                    .get(0)                      // 첫 번째 결과
                    .path("_source")
                    .path("road_traffic");


            return roadTraffic; // 외부 API에서 받은 데이터를 그대로 반환
        }catch (InvalidRequestException e) {
            throw new InvalidRequestException(e.getMessage(), e);
        }catch (Exception e){
            System.out.println(e);
            throw new RuntimeException("예상치 못한 오류",e);
        }
    }
}
