package com.example.userservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SuggestRequestDto {
    @JsonProperty("question_type")
    private int questionType;

    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("finish_time")
    private String finishTime;

    @JsonProperty("start_place")
    private String startPlace;

    @JsonProperty("optional_request")
    private String optionalRequest;
}
