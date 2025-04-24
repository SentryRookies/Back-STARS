package com.example.placeservice.service;

import com.example.placeservice.dto.AccommodationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class AccommodationService {
    private final AccommodationParseService accommodationParseService;

    @Autowired
    public AccommodationService(AccommodationParseService accommodationParseService) {
        this.accommodationParseService = accommodationParseService;
    }

    public List<AccommodationResponse.Body.Items.Item> getAllAccommodationData() throws IOException {
        return accommodationParseService.parseXmlData();
    }
}
