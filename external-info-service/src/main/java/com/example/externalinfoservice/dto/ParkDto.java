package com.example.externalinfoservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

@Data
@XmlRootElement(name = "SeoulRtd.citydata")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkDto {

    @XmlElement(name = "CITYDATA")
    private CityData cityData;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CityData {

        @XmlElement(name = "PRK_STTS")
        private PrkSttsWrapper prktts;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PrkSttsWrapper {

        @XmlElement(name = "PRK_STTS")
        private List<ParkingStatus> prkStts;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ParkingStatus {

        @XmlElement(name = "PRK_NM")
        private String prk_nm;

        @XmlElement(name = "PRK_CD")
        private String prk_cd;

        @XmlElement(name = "PRK_TYPE")
        private String prk_type;

        @XmlElement(name = "CPCTY")
        private int cpcty;

        @XmlElement(name = "CUR_PRK_CNT")
        private String cur_prk_cnt;

        @XmlElement(name = "CUR_PRK_TIME")
        private String cur_prk_time;

        @XmlElement(name = "CUR_PRK_YN")
        private String cur_prk_yn;

        @XmlElement(name = "PAY_YN")
        private String pay_yn;

        @XmlElement(name = "RATES")
        private int rates;

        @XmlElement(name = "TIME_RATES")
        private int time_rates;

        @XmlElement(name = "ADD_RATES")
        private int add_rates;

        @XmlElement(name = "ADD_TIME_RATES")
        private int add_time_rates;
    }
}
