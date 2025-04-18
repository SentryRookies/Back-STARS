package com.example.placeservice.service;

import com.example.placeservice.dto.congestion.CongestionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringReader;

@Service
public class CongestionService {

    @Value("http://openapi.seoul.go.kr:8088/sample/xml/citydata_ppltn/1/5/")
    private String openApiUrl;

    private final RestTemplate restTemplate;

    public CongestionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getCongestion(String areaName) {
        try{
            // 1. 외부 OpenAPI에서 XML 데이터 받아오기
            String xmlResponse = restTemplate.getForObject(openApiUrl + areaName, String.class);

            // 2. XML 데이터를 DTO로 변환 -> JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(xmlToDto(xmlResponse));

            return json;

        }catch (InvalidRequestException e) {
            throw new InvalidRequestException(e.getMessage(), e);
        } catch(IOException e){
            throw new RuntimeException("외부 API 오류",e);
        } catch (Exception e) {
            throw new RuntimeException("예상치 못한 오류",e);
        }
    }

    private CongestionDto xmlToDto(String xml) throws Exception {
        // JAXB를 사용해서 XML을 DTO로 변환
        JAXBContext context = JAXBContext.newInstance(CongestionDto.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (CongestionDto) unmarshaller.unmarshal(new StringReader(xml));
    }
}
