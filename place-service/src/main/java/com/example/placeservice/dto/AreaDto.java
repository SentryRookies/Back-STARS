package com.example.placeservice.dto;

import com.example.placeservice.entity.Area;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AreaDto {
    private Long area_id;
    private String seoul_id;
    private String area_name;
    private String name_eng;
    private String category;
    private BigDecimal lat;
    private BigDecimal lon;

    public AreaDto(Long areaId, String seoulId, String area_name, String nameEng, String category, BigDecimal lat, BigDecimal lon) {
        this.area_id = areaId;
        this.seoul_id = seoulId;
        this.area_name = area_name;
        this.name_eng = nameEng;
        this.category = category;
        this.lat = lat;
        this.lon = lon;
    }
}
