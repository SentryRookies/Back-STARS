package com.example.externalinfoservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "SeoulRtd.citydata")
@XmlAccessorType(XmlAccessType.FIELD)
public class WeatherDtoOriginal {
    @XmlElement(name = "WEATHER_STTS")
    @JsonProperty("weather_status")
    private WeatherStatus weatherStatus;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class WeatherStatus {
        @XmlElement(name = "TEMP")
        @JsonProperty("temperature")
        private Double temperature;


        @XmlElement(name = "PRECIPITATION")
        @JsonProperty("precipitation")
        private Double precipitation;

        @XmlElement(name = "PRECPT_TYPE")
        @JsonProperty("precipitation_type")
        private String precipitationType;

        @XmlElement(name = "PM10")
        @JsonProperty("pm10")
        private Integer pm10;

        @XmlElement(name = "FCST24HOURS")
        @JsonProperty("forecast24hours")
        private String forecast24hours;

        @XmlElement(name = "SKY_STTS")
        @JsonProperty("sky_status")
        private String skyStatus;
    }
}