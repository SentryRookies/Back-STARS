package com.example.placeservice.service;

import com.example.placeservice.dto.accommodation.AccommodationDto;
import com.example.placeservice.dto.PlaceDocument;
import com.example.placeservice.service.PlaceDocumentMapper;
import com.example.placeservice.entity.*;
import com.example.placeservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceDocumentService {

    private final AccommodationRepository accommodationRepository;
    private final RestaurantRepository restaurantRepository;
    private final AttractionRepository attractionRepository;
    private final CulturalEventRepository culturalEventRepository;
    private final CafeRepository cafeRepository;

    private final PlaceDocumentMapper placeDocumentMapper;

    public List<PlaceDocument> searchPlacesByName(String keyword) {
        List<PlaceDocument> results = new ArrayList<>();

        // Accommodation, Attraction, Cafe, Restaurant, CulturalEvent에서 이름이 포함된 객체만 필터링
        results.addAll(accommodationRepository.findByNameContainingIgnoreCase(keyword)
                .stream().map(placeDocumentMapper::toPlaceDocument).toList());

        results.addAll(attractionRepository.findByNameContainingIgnoreCase(keyword)
                .stream().map(placeDocumentMapper::toPlaceDocument).toList());

        results.addAll(cafeRepository.findByNameContainingIgnoreCase(keyword)
                .stream().map(placeDocumentMapper::toPlaceDocument).toList());

        results.addAll(restaurantRepository.findByNameContainingIgnoreCase(keyword)
                .stream().map(placeDocumentMapper::toPlaceDocument).toList());

        results.addAll(culturalEventRepository.findBytitleContainingIgnoreCase(keyword)
                .stream().map(placeDocumentMapper::toPlaceDocument).toList());

        // 이름에만 keyword가 포함된 것만 남긴다.
        return results.stream()
                .filter(place -> place.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<PlaceDocument> searchPlacesByAddress(String keyword) {
        List<PlaceDocument> results = new ArrayList<>();

        // Accommodation, Attraction, Cafe, Restaurant, CulturalEvent에서 주소가 포함된 객체만 필터링
        results.addAll(accommodationRepository.findByAddressContainingIgnoreCase(keyword)
                .stream().map(placeDocumentMapper::toPlaceDocument).toList());

        results.addAll(attractionRepository.findByAddressContainingIgnoreCase(keyword)
                .stream().map(placeDocumentMapper::toPlaceDocument).toList());

        results.addAll(cafeRepository.findByAddressContainingIgnoreCase(keyword)
                .stream().map(placeDocumentMapper::toPlaceDocument).toList());

        results.addAll(restaurantRepository.findByAddressContainingIgnoreCase(keyword)
                .stream().map(placeDocumentMapper::toPlaceDocument).toList());

        results.addAll(culturalEventRepository.findByAddressContainingIgnoreCase(keyword)
                .stream().map(placeDocumentMapper::toPlaceDocument).toList());

        // 주소에만 keyword가 포함된 것만 남긴다.
        return results.stream()
                .filter(place -> place.getAddress().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }


}
