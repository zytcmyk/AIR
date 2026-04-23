package com.airquality.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 污染源分布DTO
 */
@Data
public class SourceDistributionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 污染源类型名称
     */
    private String name;

    /**
     * 贡献率
     */
    private BigDecimal value;
}
