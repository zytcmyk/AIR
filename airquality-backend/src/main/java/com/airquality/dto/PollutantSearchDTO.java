package com.airquality.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 污染物搜索条件DTO
 */
@Data
public class PollutantSearchDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 城市名
     */
    private String city;

    /**
     * 污染物类型（PM2.5、PM10、SO2、NO2、O3、CO）
     */
    private String pollutantType;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}
