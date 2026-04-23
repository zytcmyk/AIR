package com.airquality.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 污染源创建DTO
 */
@Data
public class SourceCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "城市不能为空")
    private String city;

    @NotBlank(message = "污染源类型不能为空")
    private String sourceType;

    @NotNull(message = "贡献率不能为空")
    private BigDecimal contributionRate;

    @NotNull(message = "日期不能为空")
    private LocalDate date;
}
