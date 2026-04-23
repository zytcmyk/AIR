package com.airquality.controller;

import com.airquality.annotation.AdminRequired;
import com.airquality.dto.ArticleCreateDTO;
import com.airquality.dto.ArticleManageDTO;
import com.airquality.dto.ArticleUpdateDTO;
import com.airquality.entity.Article;
import com.airquality.service.ArticleService;
import com.airquality.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 文章管理控制器（管理员）
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/article")
@AdminRequired
public class AdminArticleController {

    @Resource
    private ArticleService articleService;

    @GetMapping("/list")
    public Result<Map<String, Object>> getArticleList(ArticleManageDTO dto) {
        Page<Article> page = articleService.getArticleList(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("list", page.getRecords());
        result.put("total", page.getTotal());
        result.put("page", page.getCurrent());
        result.put("size", page.getSize());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Article> getArticleById(@PathVariable Long id) {
        Article article = articleService.getArticleById(id);
        return Result.success(article);
    }

    @PostMapping
    public Result<Void> createArticle(@RequestBody ArticleCreateDTO dto) {
        try {
            articleService.createArticle(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> updateArticle(@PathVariable Long id, @RequestBody ArticleUpdateDTO dto) {
        dto.setId(id);
        try {
            articleService.updateArticle(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success();
    }

    @PutMapping("/{id}/publish")
    public Result<Void> publishArticle(@PathVariable Long id, @RequestParam Integer status) {
        try {
            articleService.publishArticle(id, status);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }
}
