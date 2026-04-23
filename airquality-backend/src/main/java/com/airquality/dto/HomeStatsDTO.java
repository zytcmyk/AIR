package com.airquality.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 首页统计数据DTO
 */
@Data
public class HomeStatsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前AQI平均值
     */
    private Integer currentAqi;

    /**
     * 空气质量等级
     */
    private String qualityLevel;

    /**
     * 环比变化（与昨日相比）
     */
    private Integer compareValue;

    /**
     * 最新日期
     */
    private String latestDate;

    /**
     * 城市数量
     */
    private Integer cityCount;

    /**
     * 记录总数
     */
    private Integer totalCount;
}
