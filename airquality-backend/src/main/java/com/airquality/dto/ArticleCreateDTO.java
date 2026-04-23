package com.airquality.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文章创建DTO
 */
@Data
public class ArticleCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;

    @NotBlank(message = "类型不能为空")
    private String type;

    private Integer status = 0;
}
