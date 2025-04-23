package com.example.placeservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CulturalEventInfo {
    private int list_total_count;
    private Result RESULT;
    @JsonProperty("row")
    private List<CulturalEventItem> row;
}
