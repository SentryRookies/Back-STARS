package com.example.placeservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AttractionListDto {
    private Long attraction_id;
    private String name;
    private String address;
    private BigDecimal lat;
    private BigDecimal lon;


    public AttractionListDto(Long attraction_id, String name, String address, BigDecimal lat, BigDecimal lon) {
        this.attraction_id = attraction_id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
    }
}
