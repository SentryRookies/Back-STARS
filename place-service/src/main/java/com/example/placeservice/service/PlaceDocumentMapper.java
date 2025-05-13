package com.example.placeservice.service;

import com.example.placeservice.dto.PlaceDocument;

import com.example.placeservice.entity.*;
import org.springframework.stereotype.Component;

@Component
public class PlaceDocumentMapper {

    public PlaceDocument toPlaceDocument(Accommodation item) {
        return PlaceDocument.builder()
                .name(item.getName())
                .address(item.getAddress())
                .type("accommodation")
                .place_id(String.valueOf(item.getAccommodationId()))
                .build();
    }

    public PlaceDocument toPlaceDocument(Cafe item) {
        return PlaceDocument.builder()
                .name(item.getName())
                .address(item.getAddress())
                .type("cafe")
                .place_id(String.valueOf(item.getId()))
                .build();
    }

    public PlaceDocument toPlaceDocument(CulturalEvent item) {
        return PlaceDocument.builder()
                .name(item.getTitle())
                .address(item.getAddress())
                .type("culturalevent")
                .place_id(String.valueOf(item.getEventId()))
                .build();
    }

    public PlaceDocument toPlaceDocument(Restaurant item) {
        return PlaceDocument.builder()
                .name(item.getName())
                .address(item.getAddress())
                .type("restaurant")
                .place_id(String.valueOf(item.getRestaurantId()))
                .build();
    }

    public PlaceDocument toPlaceDocument(Attraction item) {
        return PlaceDocument.builder()
                .name(item.getName())
                .address(item.getAddress())
                .type("attraction")
                .place_id(String.valueOf(item.getAttractionId()))
                .build();
    }
}
