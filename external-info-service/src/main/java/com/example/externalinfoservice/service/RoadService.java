package com.example.externalinfoservice.service;

import com.example.externalinfoservice.dto.RoadDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;

@Service
public class RoadService {

    @Value("http://openapi.seoul.go.kr:8088/sample/xml/citydata/1/5/")
    private String openApiUrl;

    private final RestTemplate restTemplate;

    public RoadService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getTrafficData(String id) {
        // 1. 외부 OpenAPI에서 XML 데이터 받아오기
        String xmlResponse = restTemplate.getForObject(openApiUrl + id, String.class);

        // 2. XML 데이터를 DTO로 변환
        try {
            // DTO -> JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(xmlToDto(xmlResponse));

            return json;
        } catch (Exception e) {
            throw new RuntimeException("XML 파싱 실패", e);
        }
    }

    private RoadDto xmlToDto(String xml) throws Exception {
        // JAXB를 사용해서 XML을 DTO로 변환
        JAXBContext context = JAXBContext.newInstance(RoadDto.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (RoadDto) unmarshaller.unmarshal(new StringReader(xml));
    }
}
