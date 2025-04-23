package com.example.placeservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CulturalEventItem {

    @JsonProperty("CODENAME")
    private String codename;

    @JsonProperty("GUNAME")
    private String guname;

    @JsonProperty("TITLE")
    private String title;

    @JsonProperty("PLACE")
    private String place;

    @JsonProperty("ORG_NAME")
    private String orgName;

    @JsonProperty("USE_TRGT")
    private String useTrgt;

    @JsonProperty("USE_FEE")
    private String useFee;

    @JsonProperty("ORG_LINK")
    private String orgLink;

    @JsonProperty("MAIN_IMG")
    private String mainImg;

    @JsonProperty("STRTDATE")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")  // 날짜 포맷 설정
    private LocalDateTime strtdate;

    @JsonProperty("END_DATE")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")  // 날짜 포맷 설정
    private LocalDateTime endDate;

    @JsonProperty("LOT")
    private BigDecimal lot;

    @JsonProperty("LAT")
    private BigDecimal lat;

    @JsonProperty("IS_FREE")
    private String isFree;

    @JsonProperty("HMPG_ADDR")
    private String hmpgAddr;
}
