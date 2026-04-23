package com.airquality.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * AQI更新DTO
 */
@Data
public class AqiUpdateDTO implements Serializable {

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
     * AQI值
     */
    private Integer aqiValue;

    /**
     * 空气质量等级
     */
    private String qualityLevel;

    /**
     * 日期
     */
    private LocalDate date;
}
