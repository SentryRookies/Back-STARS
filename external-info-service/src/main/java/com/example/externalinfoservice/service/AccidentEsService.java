package com.example.externalinfoservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.beans.factory.annotation.Value;
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

    private final RestTemplate restTemplate = new RestTemplate(); // 주입 방식으로 대체해도 OK
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("elastic_url")
    private static String elastic_url;

    public JsonNode getAccidentData() {
        try {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String apiUrl = String.format(elastic_url + "/seoul_citydata_accident_%s/_search", today);

            String jsonBody = """
                {
                  "query": {
                    "bool": {
                      "must": [
                        {
                          "range": {
                            "accident.exp_clr_dt": {
                              "gt": "now"
                            }
                          }
                        }
                      ]
                    }
                  },
                  "collapse": {
                    "field": "accident.search"
                  },
                  "size": 100
                }
                """;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
            String body = response.getBody();

            JsonNode root = objectMapper.readTree(body);
            JsonNode hits = root.path("hits").path("hits");

            List<JsonNode> accidentList = new ArrayList<>();
            for (JsonNode hit : hits) {
                JsonNode accident = hit.path("_source").path("accident");
                accidentList.add(accident);
            }

            return objectMapper.valueToTree(accidentList);

        } catch (InvalidRequestException e) {
            throw new InvalidRequestException(e.getMessage(), e);
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException("사고 현황 : 예상치 못한 오류", e);
        }
    }

    public List<Map<String, Object>> getAllAccidentsFromES() {
        try {
            JsonNode accidentArray = getAccidentData();
            List<Map<String, Object>> list = new ArrayList<>();

            for (JsonNode node : accidentArray) {
                Map<String, Object> map = objectMapper.convertValue(node, Map.class);
                list.add(map);
            }

            return list;
        } catch (Exception e) {
            throw new RuntimeException("사고 데이터 변환 실패", e);
        }
    }
}
