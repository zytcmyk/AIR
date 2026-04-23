package com.airquality.controller;

import com.airquality.dto.ArticleDetailVO;
import com.airquality.dto.ArticleVO;
import com.airquality.service.ArticleService;
import com.airquality.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文章控制器（前台，无需认证）
 */
@RestController
@RequestMapping("/api/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    /**
     * 前台分页查询已发布文章
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getArticleList(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ArticleVO> pageObj = articleService.getPublishedArticleList(type, page, size);
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageObj.getRecords());
        result.put("total", pageObj.getTotal());
        result.put("page", pageObj.getCurrent());
        result.put("size", pageObj.getSize());
        return Result.success(result);
    }

    /**
     * 获取文章详情
     */
    @GetMapping("/{id}")
    public Result<ArticleDetailVO> getArticleById(@PathVariable Long id) {
        ArticleDetailVO article = articleService.getPublishedArticleById(id);
        if (article == null) {
            return Result.error(404, "文章不存在");
        }
        // 增加阅读量
        articleService.incrementViewCount(id);
        // 更新返回的阅读量
        article.setViewCount(article.getViewCount() + 1);
        return Result.success(article);
    }

    /**
     * 获取最新文章
     */
    @GetMapping("/latest")
    public Result<List<ArticleVO>> getLatestArticles(@RequestParam(defaultValue = "5") Integer limit) {
        List<ArticleVO> list = articleService.getLatestArticles(limit);
        return Result.success(list);
    }

    /**
     * 获取置顶文章（重要预警）
     */
    @GetMapping("/top")
    public Result<List<ArticleVO>> getTopArticles() {
        List<ArticleVO> list = articleService.getTopArticles();
        return Result.success(list);
    }

    /**
     * 获取相关推荐
     */
    @GetMapping("/{id}/related")
    public Result<List<ArticleVO>> getRelatedArticles(
            @PathVariable Long id,
            @RequestParam(defaultValue = "3") Integer limit) {
        List<ArticleVO> list = articleService.getRelatedArticles(id, limit);
        return Result.success(list);
    }
}
