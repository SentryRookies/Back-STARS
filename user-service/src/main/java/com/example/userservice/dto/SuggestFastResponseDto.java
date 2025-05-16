package com.example.userservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SuggestFastResponseDto {
    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("finish_time")
    private String finishTime;

    @JsonProperty("start_place")
    private String startPlace;

    @JsonProperty("optional_request")
    private String optionalRequest;

    @JsonProperty("birth_year")
    private int birthYear;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("mbti")
    private String mbti;

    @JsonProperty("answer")
    private String answer;

    @JsonProperty("created_at")
    private String createdAt;
}
