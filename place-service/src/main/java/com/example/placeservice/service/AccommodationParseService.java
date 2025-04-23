package com.example.placeservice.service;

import com.example.placeservice.dto.AccommodationResponse;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;

import jakarta.xml.bind.JAXBContext;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccommodationParseService {
    public List<AccommodationResponse.Body.Items.Item> parseXmlData(String xmlData) {
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

    public void SaveXmlData(String xmlData) {
        List<AccommodationResponse.Body.Items.Item> items = parseXmlData(xmlData);

        // 각 아이템 정보 출력
        for (AccommodationResponse.Body.Items.Item item : items) {
            System.out.println("호텔명: " + item.getTitle());
            System.out.println("주소: " + item.getAddr1() + " " + item.getAddr2());
            System.out.println("전화번호: " + item.getTel());
            System.out.println("---------------------------");
        }
    }
}