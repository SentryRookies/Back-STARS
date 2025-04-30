package com.example.placeservice.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PlaceDocument {

    // 공통
    private String name;
    private String category;
    private String address;
}
