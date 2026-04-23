package com.airquality.service;

import com.airquality.dto.*;
import com.airquality.entity.Article;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 文章服务接口
 */
public interface ArticleService {

    // 管理端
    Page<Article> getArticleList(ArticleManageDTO dto);
    Article getArticleById(Long id);
    void createArticle(ArticleCreateDTO dto);
    void updateArticle(ArticleUpdateDTO dto);
    void deleteArticle(Long id);
    void publishArticle(Long id, Integer status);

    // 前台
    Page<ArticleVO> getPublishedArticleList(String type, Integer page, Integer size);
    ArticleDetailVO getPublishedArticleById(Long id);
    List<ArticleVO> getLatestArticles(int limit);
    List<ArticleVO> getTopArticles();
    List<ArticleVO> getRelatedArticles(Long id, int limit);
    void incrementViewCount(Long id);
}
