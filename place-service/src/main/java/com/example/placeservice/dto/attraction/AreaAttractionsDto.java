package com.example.placeservice.dto.attraction;

import lombok.Data;

import java.util.List;

@Data
public class AreaAttractionsDto {
    private String area_name; // Area 이름
    private List<AttractionListDto> attraction_list;

}
