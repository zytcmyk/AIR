package com.airquality.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 污染物创建DTO
 */
@Data
public class PollutantCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 城市
     */
    @NotBlank(message = "城市不能为空")
    private String city;

    /**
     * 污染物类型
     */
    @NotBlank(message = "污染物类型不能为空")
    private String pollutantType;

    /**
     * 浓度值
     */
    @NotNull(message = "浓度值不能为空")
    private BigDecimal concentration;

    /**
     * 单位
     */
    private String unit;

    /**
     * 日期
     */
    @NotNull(message = "日期不能为空")
    private LocalDate date;
}
