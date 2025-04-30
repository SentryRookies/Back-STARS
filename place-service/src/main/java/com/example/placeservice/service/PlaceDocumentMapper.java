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
                .category("accommodation")
                .build();
    }

    public PlaceDocument toPlaceDocument(Cafe item) {
        return PlaceDocument.builder()
                .name(item.getName())
                .address(item.getAddress())
                .category("cafe")
                .build();
    }

    public PlaceDocument toPlaceDocument(CulturalEvent item) {
        return PlaceDocument.builder()
                .name(item.getTitle())
                .address(item.getAddress())
                .category("culturalevent")
                .build();
    }

    public PlaceDocument toPlaceDocument(Restaurant item) {
        return PlaceDocument.builder()
                .name(item.getName())
                .address(item.getAddress())
                .category("restaurant")
                .build();
    }

    public PlaceDocument toPlaceDocument(Attraction item) {
        return PlaceDocument.builder()
                .name(item.getName())
                .address(item.getAddress())
                .category("attraction")
                .build();
    }
}
