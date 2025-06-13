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

    public AreaDto(Area area) {
        this.area_id = area.getAreaId();
        this.seoul_id = area.getSeoulId();
        this.area_name = area.getName();
        this.name_eng = area.getNameEng();
        this.category = area.getCategory();
        this.lat = area.getLat();
        this.lon = area.getLon();
    }
}
