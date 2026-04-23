package com.airquality.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * AQI排名DTO
 */
@Data
public class AqiRankingDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 城市名
     */
    private String city;

    /**
     * AQI数值
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
}
