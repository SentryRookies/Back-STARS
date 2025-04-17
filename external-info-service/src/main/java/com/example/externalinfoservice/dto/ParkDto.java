package com.example.externalinfoservice.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

@Data
@XmlRootElement(name = "SeoulRtd.citydata")
@XmlAccessorType(XmlAccessType.FIELD)
public class ParkDto {

    @XmlElement(name = "CITYDATA")
    private CityData cityData;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CityData {

        @XmlElement(name = "PRK_STTS")
        private List<ParkingStatus> prkStts;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ParkingStatus {

        @XmlElement(name = "PRK_NM")
        private String PRK_NM;

        @XmlElement(name = "PRK_CD")
        private String PRK_CD;

        @XmlElement(name = "PRK_TYPE")
        private String PRK_TYPE;

        @XmlElement(name = "CPCTY")
        private int CPCTY;

        @XmlElement(name = "CUR_PRK_CNT")
        private Integer CUR_PRK_CNT;

        @XmlElement(name = "CUR_PRK_TIME")
        private String CUR_PRK_TIME;

        @XmlElement(name = "CUR_PRK_YN")
        private String CUR_PRK_YN;

        @XmlElement(name = "PAY_YN")
        private String PAY_YN;

        @XmlElement(name = "RATES")
        private int RATES;

        @XmlElement(name = "TIME_RATES")
        private int TIME_RATES;

        @XmlElement(name = "ADD_RATES")
        private int ADD_RATES;

        @XmlElement(name = "ADD_TIME_RATES")
        private int ADD_TIME_RATES;

    }
}
