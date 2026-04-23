package com.airquality.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * AQI搜索条件DTO
 */
@Data
public class AqiSearchDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 城市名
     */
    private String city;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}
