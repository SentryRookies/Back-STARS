package com.example.placeservice.dto.attraction;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AttractionListDto {
    private Long attraction_id;
    private String seoul_attraction_id;
    private String attraction_name;
    private String address;
    private BigDecimal lat;
    private BigDecimal lon;
    private String kakaomap_url;


    public AttractionListDto(Long attraction_id, String seoul_attraction_id, String name, String address, BigDecimal lat, BigDecimal lon, String kakaomapUrl) {
        this.attraction_id = attraction_id;
        this.seoul_attraction_id = seoul_attraction_id;
        this.attraction_name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.kakaomap_url = kakaomapUrl;
    }
}
