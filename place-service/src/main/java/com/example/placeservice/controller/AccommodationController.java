package com.example.placeservice.controller;

import com.example.placeservice.dto.AccommodationResponse;
import com.example.placeservice.service.AccommodationSaveService;
import com.example.placeservice.service.AccommodationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@RestController
@RequestMapping("/main/accommodation")
public class AccommodationController {
    private final String serviceKey = "XxaElYAH0Vl95pt1hDBfkfBYcie4dR%2BbrJbtCdzzmvbVwAIeVnebWMpiZOD0OqkjMZgr39aEFnTBGJ8jjSmEtQ%3D%3D";
    private final String baseApiUrl = "http://apis.data.go.kr/B551011/KorService1/searchStay1";
    private final AccommodationService accommodationService;
    private final AccommodationSaveService accommodationSaveService;

    // 생성자 주입
    public AccommodationController(AccommodationService accommodationService, AccommodationSaveService accommodationSaveService) {
        this.accommodationService = accommodationService;
        this.accommodationSaveService = accommodationSaveService;
    }

    @GetMapping("/list")
    public List<AccommodationResponse.Body.Items.Item> getAccommodations() throws IOException {
        // API 요청 URL 구성
        String apiUrl = baseApiUrl + "?arrange=A&areaCode=1&ServiceKey=" + serviceKey
                + "&listYN=Y&MobileOS=ETC&MobileApp=AppTest&numOfRows=500&pageNo=1";

        // HTTP 연결 및 데이터 가져오기
        String xmlData = fetchXmlData(apiUrl);

        // 서비스를 통해 XML 데이터 파싱
        return accommodationService.getAccommodationData(xmlData);
    }

    // XML 데이터를 가져오는 메소드
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
}
