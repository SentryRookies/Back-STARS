package com.example.placeservice.dto.congestion;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@XmlRootElement(name = "Map")
@XmlAccessorType(XmlAccessType.FIELD)
public class CongestionDto {

    @XmlElement(name="SeoulRtd.citydata_ppltn")
    @JsonProperty("ppltn_data")
    private PpltnData ppltnData;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PpltnData {

        @XmlElement(name = "AREA_NM")
        @JsonProperty("area_nm")
        private String areaName;

        @XmlElement(name = "AREA_CD")
        @JsonProperty("area_cd")
        private String areaCode;

        @XmlElement(name = "AREA_CONGEST_LVL")
        @JsonProperty("area_congest_lvl")
        private String areaCongestLvl;

        @XmlElement(name = "AREA_CONGEST_MSG")
        @JsonProperty("area_congest_msg")
        private String areaCongestionMsg;

        @XmlElement(name = "AREA_PPLTN_MIN")
        @JsonProperty("area_ppltn_min")
        private int areaPpltnMin;

        @XmlElement(name = "AREA_PPLTN_MAX")
        @JsonProperty("area_ppltn_max")
        private int areaPpltnMax;

        @XmlElement(name = "MALE_PPLTN_RATE")
        @JsonProperty("male_ppltn_rate")
        private double malePpltnRate;

        @XmlElement(name = "FEMALE_PPLTN_RATE")
        @JsonProperty("female_ppltn_rate")
        private double femalePpltnRate;

        @XmlElement(name = "PPLTN_RATE_0")
        @JsonProperty("ppltn_rate_0")
        private double PpltnRate0;

        @XmlElement(name = "PPLTN_RATE_10")
        @JsonProperty("ppltn_rate_10")
        private double PpltnRate10;

        @XmlElement(name = "PPLTN_RATE_20")
        @JsonProperty("ppltn_rate_20")
        private double PpltnRate20;

        @XmlElement(name = "PPLTN_RATE_30")
        @JsonProperty("ppltn_rate_30")
        private double PpltnRate30;

        @XmlElement(name = "PPLTN_RATE_40")
        @JsonProperty("ppltn_rate_40")
        private double PpltnRate40;

        @XmlElement(name = "PPLTN_RATE_50")
        @JsonProperty("ppltn_rate_50")
        private double PpltnRate50;

        @XmlElement(name = "PPLTN_RATE_60")
        @JsonProperty("ppltn_rate_60")
        private double PpltnRate60;

        @XmlElement(name = "PPLTN_RATE_70")
        @JsonProperty("ppltn_rate_70")
        private double PpltnRate70;

        @XmlElement(name = "RESNT_PPLTN_RATE")
        @JsonProperty("resnt_ppltn_rate")
        private double ResntPpltnRate;

        @XmlElement(name = "NON_RESNT_PPLTN_RATE")
        @JsonProperty("non_resnt_ppltn_rate")
        private double NonResntPpltnRate;

        @XmlElement(name = "REPLACE_YN")
        @JsonProperty("replace_yn")
        private String replaceYn;

        @XmlElement(name = "PPLTN_TIME")
        @JsonProperty("ppltn_time")
        private String ppltnTime;

        @XmlElement(name = "FCST_YN")
        @JsonProperty("fcst_yn")
        private String fcstYn;

        @XmlElement(name = "FCST_PPLTN")
        @JsonProperty("fcst_ppltn_wrapper")
        private FcstPpltnWrapper FcstPpltnWrapper;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class FcstPpltnWrapper {
        @XmlElement(name = "FCST_PPLTN")
        @JsonProperty("fcst_ppltn")
        private List<FcstPpltn> FcstPpltn;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class FcstPpltn {
        @XmlElement(name = "FCST_TIME")
        @JsonProperty("fcst_time")
        private String fcstTime;

        @XmlElement(name = "FCST_CONGEST_LVL")
        @JsonProperty("fcst_congest_lvl")
        private String fcstCongestLvl;

        @XmlElement(name = "FCST_PPLTN_MIN")
        @JsonProperty("fcst_ppltn_min")
        private int fcstPpltnMin;

        @XmlElement(name = "FCST_PPLTN_MAX")
        @JsonProperty("fcst_ppltn_max")
        private int fcstPpltnMax;

    }




}
