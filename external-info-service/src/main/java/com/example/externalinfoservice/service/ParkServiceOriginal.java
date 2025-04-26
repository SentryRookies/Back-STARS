package com.example.externalinfoservice.service;

import com.example.externalinfoservice.dto.ParkDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;

@Service
public class ParkServiceOriginal {

    @Value("${seoul.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public ParkServiceOriginal(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getParkingData(String areaId) {
        try {
            String url = String.format(
                    "http://openapi.seoul.go.kr:8088/%s/xml/citydata/1/5/%s",
                    apiKey, areaId
            );

            // XML 요청
            String xmlResponse = restTemplate.getForObject(url, String.class);

            // XML -> DTO
            ParkDto parkDto = xmlToDto(xmlResponse);

            // DTO -> JSON
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(parkDto);

        } catch (Exception e) {
            throw new RuntimeException("주차장 데이터 파싱 실패: " + e.getMessage(), e);
        }
    }

    private ParkDto xmlToDto(String xml) throws Exception {
        JAXBContext context = JAXBContext.newInstance(ParkDto.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (ParkDto) unmarshaller.unmarshal(new StringReader(xml));
    }
}
