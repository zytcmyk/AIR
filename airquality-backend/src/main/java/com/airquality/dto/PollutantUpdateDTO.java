package com.airquality.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 污染物更新DTO
 */
@Data
public class PollutantUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 城市
     */
    private String city;

    /**
     * 污染物类型
     */
    private String pollutantType;

    /**
     * 浓度值
     */
    private BigDecimal concentration;

    /**
     * 单位
     */
    private String unit;

    /**
     * 日期
     */
    private LocalDate date;
}
