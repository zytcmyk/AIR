package com.airquality.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 污染物趋势DTO
 */
@Data
public class PollutantTrendDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    private String date;

    /**
     * 浓度值
     */
    private BigDecimal value;
}
