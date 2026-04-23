package com.airquality.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 污染源更新DTO
 */
@Data
public class SourceUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "ID不能为空")
    private Long id;

    private String city;
    private String sourceType;
    private BigDecimal contributionRate;
    private LocalDate date;
}
