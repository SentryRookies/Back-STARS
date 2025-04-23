package com.example.placeservice.config;

import com.example.placeservice.dto.AccommodationResponse;
import com.example.placeservice.service.AccommodationParseService;
import com.example.placeservice.service.AccommodationSaveService;
import com.example.placeservice.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final AccommodationSaveService accommodationSaveService;
    private final AccommodationParseService accommodationParseService;
    private final String apiUrl = "http://apis.data.go.kr/B551011/KorService1/searchStay1?arrange=A&areaCode=1&ServiceKey=XxaElYAH0Vl95pt1hDBfkfBYcie4dR%2BbrJbtCdzzmvbVwAIeVnebWMpiZOD0OqkjMZgr39aEFnTBGJ8jjSmEtQ%3D%3D&listYN=Y&MobileOS=ETC&MobileApp=AppTest&arrange=A&numOfRows=500&pageNo=1";

    @Override
    public void run(String... args) throws Exception {
        // API 호출 및 XML 파싱
        String xmlData = fetchXmlData(apiUrl);
        List<AccommodationResponse.Body.Items.Item> items = accommodationParseService.parseXmlData(xmlData);

        // 파싱된 데이터 저장
        accommodationSaveService.saveAccommodation(items);

        System.out.println("데이터 초기화가 완료되었습니다.");
    }

    private String fetchXmlData(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder xmlData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                xmlData.append(line);
            }
            return xmlData.toString();
        } finally {
            connection.disconnect(); // 연결 해제
        }
    }
/*
    private String fetchDataFromApi() {
        // API 호출 코드
        // 예시: RestTemplate 사용
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(apiUrl, String.class);
    }

*/
}