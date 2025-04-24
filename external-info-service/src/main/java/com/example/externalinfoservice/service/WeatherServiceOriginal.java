package com.example.externalinfoservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

@Service
public class WeatherServiceOriginal {

    private final String weatherApiUrl;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 날씨 관련 필드 목록
    private static final Set<String> WEATHER_FIELDS = new HashSet<>() {{
        add("TEMP");           // 기온
        add("PRECIPITATION");  // 강수량
        add("PRECPT_TYPE");    // 강수 형태
        add("PM10");           // 미세먼지
        add("FCST24HOURS");    // 24시간 예보
        add("SKY_STTS");       // 하늘 상태
    }};

    public WeatherServiceOriginal(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.weatherApiUrl = "http://openapi.seoul.go.kr:8088/594b4a6559796f683930466466666d/xml/citydata/1/5/";
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String getWeatherData(String placeCode) {
        // 1. 외부 OpenAPI에서 XML 데이터 받아오기
        String xmlResponse = restTemplate.getForObject(weatherApiUrl + placeCode, String.class);

        // 전체 XML 응답 출력
        System.out.println("XML Response: " + xmlResponse);

        try {
            // XML 파싱을 위한 DocumentBuilder 생성
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes()));

            // 모든 노드 이름 출력
            NodeList allNodes = document.getElementsByTagName("*");
            System.out.println("Total nodes: " + allNodes.getLength());
            for (int i = 0; i < allNodes.getLength(); i++) {
                System.out.println("Node " + i + ": " + allNodes.item(i).getNodeName());
            }

            // JSON 객체 생성
            ObjectNode weatherJson = objectMapper.createObjectNode();
            ObjectNode weatherSttsJson = objectMapper.createObjectNode();

            // 원하는 필드들
            String[] desiredFields = {
                    "TEMP", "PRECIPITATION", "PRECPT_TYPE",
                    "PM10", "FCST24HOURS", "SKY_STTS"
            };

            // 모든 WEATHER_STTS 하위 노드 탐색
            NodeList weatherSttsNodes = document.getElementsByTagName("WEATHER_STTS");
            System.out.println("WEATHER_STTS nodes count: " + weatherSttsNodes.getLength());

            // 각 필드별로 특별 처리
            for (String field : desiredFields) {
                NodeList fieldNodes = document.getElementsByTagName(field);
                System.out.println(field + " nodes count: " + fieldNodes.getLength());

                // 각 필드에 대해 값 추출
                if (fieldNodes.getLength() > 0) {
                    Node fieldNode = fieldNodes.item(0);
                    String nodeValue = fieldNode.getTextContent().trim();
                    System.out.println(field + " value: " + nodeValue);

                    if (field.equals("FCST24HOURS")) {
                        // FCST24HOURS는 배열로 처리
                        ArrayNode forecastArray = objectMapper.createArrayNode();
                        NodeList fcstChildren = fieldNode.getChildNodes();

                        for (int j = 0; j < fcstChildren.getLength(); j++) {
                            Node fcstChild = fcstChildren.item(j);
                            if (fcstChild.getNodeType() == Node.ELEMENT_NODE) {
                                Element fcstElement = (Element) fcstChild;
                                ObjectNode forecastItem = objectMapper.createObjectNode();
                                NodeList itemChildren = fcstElement.getChildNodes();

                                for (int k = 0; k < itemChildren.getLength(); k++) {
                                    Node itemChild = itemChildren.item(k);
                                    if (itemChild.getNodeType() == Node.ELEMENT_NODE) {
                                        Element itemElement = (Element) itemChild;
                                        String itemNodeName = itemElement.getNodeName();
                                        String itemNodeValue = itemElement.getTextContent().trim();

                                        if (!itemNodeValue.isEmpty()) {
                                            forecastItem.put(convertToCamelCase(itemNodeName), itemNodeValue);
                                        }
                                    }
                                }

                                if (!forecastItem.isEmpty()) {
                                    forecastArray.add(forecastItem);
                                }
                            }
                        }

                        weatherSttsJson.set(convertToCamelCase(field), forecastArray);
                    } else if (!nodeValue.isEmpty()) {
                        weatherSttsJson.put(convertToCamelCase(field), nodeValue);
                    }
                }
            }

            // weatherStts 필드에 추출된 정보 저장
            weatherJson.set("weatherStts", weatherSttsJson);

            // JSON을 예쁘게 출력 (들여쓰기 2칸)
            String jsonString = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(weatherJson);
    // JSON 문자열에 줄바꿈 문자가 제대로 유지되는지 확인
            System.out.println("Formatted JSON: " + jsonString);
            return jsonString;

        } catch (Exception e) {
            e.printStackTrace(); // 전체 스택 트레이스 출력
            throw new RuntimeException("날씨 데이터 변환 실패", e);
        }
    }

    // 언더스코어 이름을 camelCase로 변환
    private String convertToCamelCase(String input) {
        if (input == null || input.isEmpty()) return input;

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;

        for (char c : input.toLowerCase().toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}