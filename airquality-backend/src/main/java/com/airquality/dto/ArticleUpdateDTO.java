package com.airquality.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 文章更新DTO
 */
@Data
public class ArticleUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "ID不能为空")
    private Long id;

    private String title;
    private String content;
    private String type;
    private Integer status;
}
