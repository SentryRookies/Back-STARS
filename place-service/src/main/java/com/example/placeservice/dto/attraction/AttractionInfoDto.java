package com.example.placeservice.dto.attraction;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AttractionInfoDto {
    private Long attraction_id;
    private String seoul_attraction_id;
    private String name;
    private String address;
    private BigDecimal lat;
    private BigDecimal lon;
    private String phone;
    private String homepage_url;
    private String close_day;
    private String use_time;
    private String kakaomap_url;

    public AttractionInfoDto(Long attractionId, String seoul_attraction_id, String name, String address, BigDecimal lat, BigDecimal lon, String phone, String homepage_url, String close_day, String use_time, String kakaomapUrl) {
        this.attraction_id = attractionId;
        this.seoul_attraction_id = seoul_attraction_id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.phone = phone;
        this.homepage_url = homepage_url;
        this.close_day = close_day;
        this.use_time = use_time;
        this.kakaomap_url = kakaomapUrl;
    }

}
