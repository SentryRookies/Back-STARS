package com.example.externalinfoservice.service;

import com.example.externalinfoservice.entity.Area;
import com.example.externalinfoservice.repository.AreaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class WeatherServiceOriginal {

    @Value("${seoul.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AreaRepository areaRepository;

    // 날씨 상태별 아이콘 URL 매핑
    private static final Map<String, String> WEATHER_ICONS = new HashMap<>() {{
        put("구름 조금", "https://data.seoul.go.kr/SeoulRtd/images/icon/weather/ico_w_halfsun.png");
        put("비", "https://data.seoul.go.kr/SeoulRtd/images/icon/weather/ico_w_rain.png");
        put("눈", "https://data.seoul.go.kr/SeoulRtd/images/icon/weather/ico_w_snow.png");
        put("구름 많음", "https://data.seoul.go.kr/SeoulRtd/images/icon/weather/ico_w_cloudy.png");
        put("맑음", "https://data.seoul.go.kr/SeoulRtd/images/icon/weather/ico_w_sunny.png");
    }};

    public WeatherServiceOriginal(RestTemplate restTemplate,
                               ObjectMapper objectMapper,
                               AreaRepository areaRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.areaRepository = areaRepository;
    }

    /**
     * 특정 지역의 상세 날씨 정보를 제공합니다.
     */
    public String getWeatherData(String placeCode) {
        try {
            // place-service 데이터베이스에서 지역 정보 조회
            Area area = areaRepository.findBySeoulId(placeCode)
                    .orElseThrow(() -> new RuntimeException("해당 지역 코드를 찾을 수 없습니다: " + placeCode));

            // 1. 외부 OpenAPI에서 XML 데이터 받아오기
            String apiUrl = String.format(
                    "http://openapi.seoul.go.kr:8088/%s/xml/citydata/1/5/%s",
                    apiKey, placeCode
            );
            String xmlResponse = restTemplate.getForObject(apiUrl, String.class);

            // XML 파싱을 위한 DocumentBuilder 생성
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes()));

            // JSON 객체 생성
            ObjectNode weatherJson = objectMapper.createObjectNode();
            ObjectNode weatherSttsJson = objectMapper.createObjectNode();

            // 지역 추가 정보
            weatherJson.put("areaName", area.getName());
            weatherJson.put("areaNameEng", area.getNameEng());
            weatherJson.put("category", area.getCategory());

            // 위도, 경도 정보 추가 (선택적)
            if (area.getLatitude() != null && area.getLongitude() != null) {
                ObjectNode locationJson = objectMapper.createObjectNode();
                locationJson.put("latitude", area.getLatitude());
                locationJson.put("longitude", area.getLongitude());
                weatherJson.set("location", locationJson);
            }

            // 원하는 필드들
            String[] desiredFields = {
                    "TEMP", "PRECIPITATION", "PRECPT_TYPE",
                    "PM10", "FCST24HOURS", "SKY_STTS"
            };

            // SKY_STTS 값을 저장할 변수
            String skyStatus = "";

            // 각 필드별로 특별 처리
            for (String field : desiredFields) {
                NodeList fieldNodes = document.getElementsByTagName(field);

                // 각 필드에 대해 값 추출
                if (fieldNodes.getLength() > 0) {
                    Node fieldNode = fieldNodes.item(0);
                    String nodeValue = fieldNode.getTextContent().trim();

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
                    } else if (field.equals("SKY_STTS")) {
                        // SKY_STTS 값 저장
                        skyStatus = nodeValue;
                        weatherSttsJson.put(convertToCamelCase(field), nodeValue);
                    } else if (!nodeValue.isEmpty()) {
                        weatherSttsJson.put(convertToCamelCase(field), nodeValue);
                    }
                }
            }

            // 날씨 상태(SKY_STTS)에 따라 아이콘 URL 매핑
            String iconUrl = WEATHER_ICONS.getOrDefault(skyStatus, "https://data.seoul.go.kr/SeoulRtd/images/icon/weather/ico_w_sun.png");

            // 매핑된 아이콘 URL 추가
            weatherSttsJson.put("weatherIcon", iconUrl);

            // weatherStts 필드에 추출된 정보 저장
            weatherJson.set("weatherStts", weatherSttsJson);

            // JSON 변환 및 반환
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(weatherJson);

        } catch (Exception e) {
            e.printStackTrace(); // 전체 스택 트레이스 출력
            throw new RuntimeException("날씨 데이터 변환 실패", e);
        }
    }

    /**
     * 모든 지역의 날씨 요약 정보를 JSON 형태로 반환합니다.
     */
    public String getAllAreasWeatherSummary() {
        try {
            // 병렬 처리를 위한 스레드 풀 생성
            ExecutorService executor = Executors.newFixedThreadPool(5);

            // 결과를 담을 객체 생성
            ObjectNode resultJson = objectMapper.createObjectNode();
            resultJson.put("title", "서울시 주요 지역 날씨 정보");

            // 전체 지역 날씨 정보를 담을 배열 생성
            ArrayNode areasWeather = objectMapper.createArrayNode();

            // place-service 데이터베이스에서 모든 지역 정보 조회
            List<Area> areas = areaRepository.findAll();

            // 모든 지역에 대해 CompletableFuture를 생성하여 병렬로 API 호출
            List<CompletableFuture<ObjectNode>> futures = areas.stream()
                    .map(area -> CompletableFuture.supplyAsync(
                            () -> getAreaWeatherSummary(area.getName(), area.getSeoulId()),
                            executor
                    ))
                    .collect(Collectors.toList());

            // 모든 CompletableFuture가 완료될 때까지 기다리고 결과 수집
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0]));

            // 결과 수집 및 JSON 배열에 추가
            allFutures.thenAccept(v ->
                    futures.forEach(future -> {
                        try {
                            ObjectNode node = future.get();
                            areasWeather.add(node);
                        } catch (Exception e) {
                            // 개별 결과 처리 중 오류 발생 시 무시하고 계속 진행
                        }
                    })
            ).get();

            // 스레드 풀 종료
            executor.shutdown();

            // 결과 JSON에 지역 날씨 정보 배열 추가
            resultJson.set("data", areasWeather);

            // JSON 변환 및 반환
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultJson);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("지역별 날씨 정보 조회 실패", e);
        }
    }

    /**
     * 지역의 간략한 날씨 정보를 추출합니다.
     */
    private ObjectNode getAreaWeatherSummary(String areaName, String areaCode) {
        try {
            // 날씨 정보 조회 API 호출
            String apiUrl = String.format(
                    "http://openapi.seoul.go.kr:8088/%s/xml/citydata/1/5/%s",
                    apiKey, areaCode
            );
            String xmlResponse = restTemplate.getForObject(apiUrl, String.class);

            // XML 파싱
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes()));

            // 필요한 정보 추출
            String temperature = getNodeValueByTagName(document, "TEMP");
            String skyStatus = getNodeValueByTagName(document, "SKY_STTS");
            String precipitationType = getNodeValueByTagName(document, "PRECPT_TYPE");

            // 날씨 아이콘 매핑
            String iconUrl = WEATHER_ICONS.getOrDefault(skyStatus,
                    "https://data.seoul.go.kr/SeoulRtd/images/icon/weather/ico_w_sun.png");

            // JSON 객체 생성 (간소화된 형식)
            ObjectNode areaWeather = objectMapper.createObjectNode();
            areaWeather.put("name", areaName);
            areaWeather.put("TEMP", temperature);
            areaWeather.put("PRECPT_TYPE", precipitationType);
            areaWeather.put("weatherIcon", iconUrl);

            return areaWeather;
        } catch (Exception e) {
            // 오류 발생 시 기본 정보 반환
            ObjectNode defaultNode = objectMapper.createObjectNode();
            defaultNode.put("name", areaName);
            defaultNode.put("TEMP", "N/A");
            defaultNode.put("PRECPT_TYPE", "정보 없음");
            defaultNode.put("weatherIcon", "https://data.seoul.go.kr/SeoulRtd/images/icon/weather/ico_w_sun.png");
            return defaultNode;
        }
    }

    /**
     * 문서에서 태그명으로 노드 값을 가져옵니다.
     */
    private String getNodeValueByTagName(Document document, String tagName) {
        NodeList nodes = document.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent().trim();
        }
        return "";
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