package com.airquality.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 城市污染贡献排名DTO
 */
@Data
public class CityRankingDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 城市名
     */
    private String city;

    /**
     * 贡献率（百分比）
     */
    private BigDecimal contributionRate;

    /**
     * 主要污染源类型
     */
    private String sourceType;

    /**
     * 排名
     */
    private Integer rank;
}
