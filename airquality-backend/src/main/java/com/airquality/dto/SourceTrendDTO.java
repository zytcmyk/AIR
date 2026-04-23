package com.airquality.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 污染源趋势DTO
 */
@Data
public class SourceTrendDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    private String date;

    /**
     * 贡献率（百分比）
     */
    private BigDecimal contributionRate;
}
