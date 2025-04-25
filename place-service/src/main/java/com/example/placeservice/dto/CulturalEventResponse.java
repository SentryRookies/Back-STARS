package com.example.placeservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CulturalEventResponse {
    @JsonProperty("culturalEventInfo")
    private CulturalEventInfo culturalEventInfo;
}
