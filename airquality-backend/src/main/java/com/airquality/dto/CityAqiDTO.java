package com.airquality.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 城市AQI数据DTO（用于地图展示）
 */
@Data
public class CityAqiDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 城市名
     */
    private String city;

    /**
     * AQI值
     */
    private Integer aqiValue;

    /**
     * 空气质量等级
     */
    private String qualityLevel;

    /**
     * 日期
     */
    private String date;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;
}
