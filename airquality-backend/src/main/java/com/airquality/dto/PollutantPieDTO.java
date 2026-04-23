package com.airquality.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 污染物饼图DTO
 */
@Data
public class PollutantPieDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 污染物类型名称
     */
    private String name;

    /**
     * 平均浓度值或占比
     */
    private BigDecimal value;
}
