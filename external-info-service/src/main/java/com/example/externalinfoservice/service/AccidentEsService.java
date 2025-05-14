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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccidentEsService {

    private static final RestTemplate restTemplate = new RestTemplate();

    public static JsonNode getAccidentData() {
        try {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String apiUrl = String.format("http://elasticsearch.seoultravel.life/seoul_citydata_accident_%s/_search", today);

            String jsonBody = "{\n" +
                    "  \"size\": 0,\n" +
                    "  \"aggs\": {\n" +
                    "    \"by_area\": {\n" +
                    "      \"terms\": {\n" +
                    "        \"field\": \"accident.area_nm\",\n" +
                    "        \"size\": 1000\n" +
                    "      },\n" +
                    "      \"aggs\": {\n" +
                    "        \"latest_hit\": {\n" +
                    "          \"top_hits\": {\n" +
                    "            \"size\": 1,\n" +
                    "            \"sort\": [\n" +
                    "              {\n" +
                    "                \"accident.get_time\": {\n" +
                    "                  \"order\": \"desc\"\n" +
                    "                }\n" +
                    "              }\n" +
                    "            ]\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

            String body = response.getBody();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body);

            JsonNode buckets = root
                    .path("aggregations")
                    .path("by_area")
                    .path("buckets");

            List<JsonNode> accidentList = new ArrayList<>();
            for (JsonNode bucket : buckets) {
                JsonNode latestHit = bucket.path("latest_hit").path("hits").path("hits").get(0);
                JsonNode accident = latestHit.path("_source").path("accident");
                accidentList.add(accident);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.valueToTree(accidentList);

        } catch (InvalidRequestException e) {
            throw new InvalidRequestException(e.getMessage(), e);
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException("예상치 못한 오류", e);
        }
    }

    public List<Map<String, Object>> getAllAccidentsFromES() {
        try {
            JsonNode accidentArray = getAccidentData();
            List<Map<String, Object>> list = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();

            for (JsonNode node : accidentArray) {
                Map<String, Object> map = mapper.convertValue(node, Map.class);
                list.add(map);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("사고 데이터 변환 실패", e);
        }
    }
}
