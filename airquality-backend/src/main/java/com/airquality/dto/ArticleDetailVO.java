package com.airquality.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章详情视图对象（前台展示）
 */
@Data
public class ArticleDetailVO {

    /**
     * 文章ID
     */
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
     * 封面图片
     */
    private String coverImage;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 阅读量
     */
    private Integer viewCount;

    /**
     * 发布时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
