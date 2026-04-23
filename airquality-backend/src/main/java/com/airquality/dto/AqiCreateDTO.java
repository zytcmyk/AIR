package com.airquality.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * AQI创建DTO
 */
@Data
public class AqiCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 城市
     */
    @NotBlank(message = "城市不能为空")
    private String city;

    /**
     * AQI值
     */
    @NotNull(message = "AQI值不能为空")
    private Integer aqiValue;

    /**
     * 空气质量等级
     */
    private String qualityLevel;

    /**
     * 日期
     */
    @NotNull(message = "日期不能为空")
    private LocalDate date;
}
