package com.airquality.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章实体类
 */
@Data
@TableName("article")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容（富文本）
     */
    private String content;

    /**
     * 类型（政策/预警/科普）
     */
    private String type;

    /**
     * 状态（0草稿 1发布）
     */
    private Integer status;

    /**
     * 是否置顶（0否 1是）
     */
    private Integer isTop;

    /**
     * 阅读量
     */
    private Integer viewCount;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 逻辑删除（0未删除 1已删除）
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
