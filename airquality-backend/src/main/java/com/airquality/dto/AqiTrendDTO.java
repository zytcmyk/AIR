package com.airquality.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * AQI趋势DTO
 */
@Data
public class AqiTrendDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    private String date;

    /**
     * AQI数值
     */
    private Integer aqiValue;
}
