package com.example.externalinfoservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ESRoadService {

    private static final RestTemplate restTemplate = new RestTemplate();

    public static JsonNode getTrafficData() {
        try {
            // 오늘 날짜 구하기
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String apiUrl = String.format("http://elasticsearch.seoultravel.life/seoul_citydata_road_%s/_search", today);

            // JSON Body 생성
            String jsonBody = String.format("{\n" +
                    "  \"size\": 0,\n" +
                    "  \"aggs\": {\n" +
                    "    \"by_area\": {\n" +
                    "      \"terms\": {\n" +
                    "        \"field\": \"road_traffic.area_nm.keyword\",\n" +
                    "        \"size\": 1000  // area_nm 종류 수만큼 충분히\n" +
                    "      },\n" +
                    "      \"aggs\": {\n" +
                    "        \"latest_hit\": {\n" +
                    "          \"top_hits\": {\n" +
                    "            \"size\": 1,\n" +
                    "            \"sort\": [\n" +
                    "              {\n" +
                    "                \"road_traffic.road_traffic_time\": {\n" +
                    "                  \"order\": \"desc\"\n" +
                    "                }\n" +
                    "              }\n" +
                    "            ]\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}\n");
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

            JsonNode buckets = root
                    .path("aggregations")
                    .path("by_area")
                    .path("buckets");

            List<JsonNode> roadTrafficList = new ArrayList<>();
            for (JsonNode bucket : buckets) {
                JsonNode latestHit = bucket.path("latest_hit").path("hits").path("hits").get(0); // 첫 번째 hit 선택
                JsonNode roadTraffic = latestHit.path("_source").path("road_traffic");
                roadTrafficList.add(roadTraffic);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode result = objectMapper.valueToTree(roadTrafficList);

            return result;

        }catch (InvalidRequestException e) {
            throw new InvalidRequestException(e.getMessage(), e);
        }catch (Exception e){
            System.out.println(e);
            throw new RuntimeException("예상치 못한 오류",e);
        }
    }
}