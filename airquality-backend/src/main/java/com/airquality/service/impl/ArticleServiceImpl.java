package com.airquality.service.impl;

import com.airquality.dto.*;
import com.airquality.entity.Article;
import com.airquality.mapper.ArticleMapper;
import com.airquality.service.ArticleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    @Override
    public Page<Article> getArticleList(ArticleManageDTO dto) {
        Page<Article> page = new Page<>(dto.getPage(), dto.getSize());
        QueryWrapper<Article> wrapper = new QueryWrapper<>();

        if (dto.getTitle() != null && !dto.getTitle().isEmpty()) {
            wrapper.like("title", dto.getTitle());
        }
        if (dto.getType() != null && !dto.getType().isEmpty()) {
            wrapper.eq("type", dto.getType());
        }
        if (dto.getStatus() != null) {
            wrapper.eq("status", dto.getStatus());
        }
        wrapper.orderByDesc("create_time");

        return articleMapper.selectPage(page, wrapper);
    }

    @Override
    public Article getArticleById(Long id) {
        return articleMapper.selectById(id);
    }

    @Override
    public void createArticle(ArticleCreateDTO dto) {
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        article.setDeleted(0);
        article.setViewCount(0);
        article.setIsTop(0);
        articleMapper.insert(article);
    }

    @Override
    public void updateArticle(ArticleUpdateDTO dto) {
        Article article = articleMapper.selectById(dto.getId());
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        if (dto.getTitle() != null) article.setTitle(dto.getTitle());
        if (dto.getContent() != null) article.setContent(dto.getContent());
        if (dto.getType() != null) article.setType(dto.getType());
        if (dto.getStatus() != null) article.setStatus(dto.getStatus());
        articleMapper.updateById(article);
    }

    @Override
    public void deleteArticle(Long id) {
        articleMapper.deleteById(id);
    }

    @Override
    public void publishArticle(Long id, Integer status) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        article.setStatus(status);
        articleMapper.updateById(article);
    }

    @Override
    public Page<ArticleVO> getPublishedArticleList(String type, Integer page, Integer size) {
        Page<Article> pageObj = new Page<>(page, size);
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1); // 已发布
        if (type != null && !type.isEmpty()) {
            wrapper.eq("type", type);
        }
        wrapper.orderByDesc("is_top").orderByDesc("create_time");

        Page<Article> articlePage = articleMapper.selectPage(pageObj, wrapper);

        // 转换为VO
        Page<ArticleVO> voPage = new Page<>(articlePage.getCurrent(), articlePage.getSize(), articlePage.getTotal());
        List<ArticleVO> voList = articlePage.getRecords().stream().map(article -> {
            ArticleVO vo = new ArticleVO();
            BeanUtils.copyProperties(article, vo);
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public ArticleDetailVO getPublishedArticleById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null || article.getStatus() != 1) {
            return null;
        }
        ArticleDetailVO vo = new ArticleDetailVO();
        BeanUtils.copyProperties(article, vo);
        return vo;
    }

    @Override
    public List<ArticleVO> getLatestArticles(int limit) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByDesc("create_time");
        wrapper.last("LIMIT " + limit);
        List<Article> articles = articleMapper.selectList(wrapper);

        return articles.stream().map(article -> {
            ArticleVO vo = new ArticleVO();
            BeanUtils.copyProperties(article, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ArticleVO> getTopArticles() {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.eq("is_top", 1);
        wrapper.orderByDesc("create_time");
        List<Article> articles = articleMapper.selectList(wrapper);

        return articles.stream().map(article -> {
            ArticleVO vo = new ArticleVO();
            BeanUtils.copyProperties(article, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ArticleVO> getRelatedArticles(Long id, int limit) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            return Collections.emptyList();
        }

        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.eq("type", article.getType());
        wrapper.ne("id", id);
        wrapper.orderByDesc("create_time");
        wrapper.last("LIMIT " + limit);
        List<Article> articles = articleMapper.selectList(wrapper);

        return articles.stream().map(a -> {
            ArticleVO vo = new ArticleVO();
            BeanUtils.copyProperties(a, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void incrementViewCount(Long id) {
        Article article = articleMapper.selectById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() == null ? 1 : article.getViewCount() + 1);
            articleMapper.updateById(article);
        }
    }
}
