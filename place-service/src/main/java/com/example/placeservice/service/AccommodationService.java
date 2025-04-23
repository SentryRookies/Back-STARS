package com.example.placeservice.service;

import com.example.placeservice.dto.AccommodationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccommodationService {
    private AccommodationParseService accommodationParseService = new AccommodationParseService();

    @Autowired
    public AccommodationService(AccommodationParseService accommodationParseService) {
        this.accommodationParseService = accommodationParseService;
    }

    public List<AccommodationResponse.Body.Items.Item> getAccommodationData(String xmlData) {
        return accommodationParseService.parseXmlData(xmlData);
    }
}
