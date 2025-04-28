package com.example.placeservice.service;

import com.example.placeservice.dto.accommodation.AccommodationResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccommodationParseService {
    private final String serviceKey;
    private final String baseApiUrl = "http://apis.data.go.kr/B551011/KorService1/searchStay1";

    public AccommodationParseService(@Value("${TOUR_API_KEY}") String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public List<AccommodationResponse.Body.Items.Item> parseXmlData() throws IOException {
        // API 요청 URL 구성
        String apiUrl = baseApiUrl + "?arrange=A&areaCode=1&ServiceKey=" + serviceKey
                + "&listYN=Y&MobileOS=ETC&MobileApp=AppTest&numOfRows=500&pageNo=1";

        // HTTP 연결 및 데이터 가져오기
        String xmlData = fetchXmlData(apiUrl);

        if (xmlData == null || xmlData.trim().isEmpty()) {
            throw new IllegalArgumentException("XML 데이터가 비어있습니다.");
        }

        try {
            // JAXB 컨텍스트 생성
            JAXBContext jaxbContext = JAXBContext.newInstance(AccommodationResponse.class);

            // Unmarshaller 생성
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // XML 문자열을 Java 객체로 변환
            StringReader reader = new StringReader(xmlData);
            AccommodationResponse accommodationResponse = (AccommodationResponse) unmarshaller.unmarshal(reader);

            // null 체크 추가
            if (accommodationResponse == null ||
                    accommodationResponse.getBody() == null ||
                    accommodationResponse.getBody().getItems() == null ||
                    accommodationResponse.getBody().getItems().getItem() == null) {
                return new ArrayList<>(); // 빈 리스트 반환
            }

            // 이제 getItem()은 List<Item>을 반환하므로 바로 사용 가능
            return accommodationResponse.getBody().getItems().getItem();

        } catch (JAXBException e) {
            throw new RuntimeException("XML 파싱 중 오류 발생: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("예상치 못한 오류 발생: " + e.getMessage(), e);
        }
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
}