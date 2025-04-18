package com.example.externalinfoservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@XmlRootElement(name = "SeoulRtd.citydata")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoadDto {

    @XmlElement(name = "CITYDATA")
    @JsonProperty("city_data")
    private CityData cityData;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CityData {
        @XmlElement(name = "ROAD_TRAFFIC_STTS")
        @JsonProperty("road_traffic_stts")
        private RoadTrafficSttsWrapper roadTrafficSttsWrapper;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class RoadTrafficSttsWrapper {
        @XmlElement(name = "AVG_ROAD_DATA")
        @JsonProperty("avg_road_data")
        private AvgRoadData avgRoadData;

        @XmlElement(name = "ROAD_TRAFFIC_STTS")
        @JsonProperty("road_traffic_stts")
        private List<RoadTrafficDetail> roadTrafficDetailList;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AvgRoadData {
        @XmlElement(name = "ROAD_MSG")
        @JsonProperty("road_msg")
        private String roadMsg;

        @XmlElement(name = "ROAD_TRAFFIC_IDX")
        @JsonProperty("road_traffic_idx")
        private String trafficIdx;

        @XmlElement(name = "ROAD_TRAFFIC_SPD")
        @JsonProperty("road_traffic_spd")
        private int trafficSpeed;

        @XmlElement(name = "ROAD_TRAFFIC_TIME")
        @JsonProperty("road_traffic_time")
        private String trafficTime;

        // Getters & Setters
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class RoadTrafficDetail {
        @XmlElement(name = "LINK_ID")
        @JsonProperty("link_id")
        private String linkId;

        @XmlElement(name = "ROAD_NM")
        @JsonProperty("road_nm")
        private String roadName;

        @XmlElement(name = "START_ND_CD")
        @JsonProperty("start_nd_cd")
        private String startNodeCode;

        @XmlElement(name = "START_ND_NM")
        @JsonProperty("start_nd_nm")
        private String startNodeName;

        @XmlElement(name = "START_ND_XY")
        @JsonProperty("start_nd_xy")
        private String startCoordinates;

        @XmlElement(name = "END_ND_CD")
        @JsonProperty("end_nd_cd")
        private String endNodeCode;

        @XmlElement(name = "END_ND_NM")
        @JsonProperty("end_nd_nm")
        private String endNodeName;

        @XmlElement(name = "END_ND_XY")
        @JsonProperty("end_nd_xy")
        private String endCoordinates;

        @XmlElement(name = "DIST")
        @JsonProperty("dist")
        private double distance;

        @XmlElement(name = "SPD")
        @JsonProperty("spd")
        private double speed;

        @XmlElement(name = "IDX")
        @JsonProperty("idx")
        private String congestionIndex;

        @XmlElement(name = "XYLIST")
        @JsonProperty("xylist")
        private String xyList;

        // Getters & Setters
    }
}
