package com.airquality.controller;

import com.airquality.annotation.AdminRequired;
import com.airquality.dto.SourceCreateDTO;
import com.airquality.dto.SourceManageDTO;
import com.airquality.dto.SourceUpdateDTO;
import com.airquality.entity.PollutionSource;
import com.airquality.service.SourceManageService;
import com.airquality.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 污染源管理控制器（管理员）
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/source")
@AdminRequired
public class AdminSourceController {

    @Resource
    private SourceManageService sourceManageService;

    @GetMapping("/list")
    public Result<Map<String, Object>> getSourceList(SourceManageDTO dto) {
        Page<PollutionSource> page = sourceManageService.getSourceList(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("list", page.getRecords());
        result.put("total", page.getTotal());
        result.put("page", page.getCurrent());
        result.put("size", page.getSize());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<PollutionSource> getSourceById(@PathVariable Long id) {
        PollutionSource data = sourceManageService.getSourceById(id);
        return Result.success(data);
    }

    @PostMapping
    public Result<Void> addSource(@RequestBody SourceCreateDTO dto) {
        try {
            sourceManageService.addSource(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> updateSource(@PathVariable Long id, @RequestBody SourceUpdateDTO dto) {
        dto.setId(id);
        try {
            sourceManageService.updateSource(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteSource(@PathVariable Long id) {
        sourceManageService.deleteSource(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> batchDeleteSource(@RequestBody List<Long> ids) {
        sourceManageService.batchDeleteSource(ids);
        return Result.success();
    }

    @GetMapping("/abnormal")
    public Result<List<PollutionSource>> getAbnormalData() {
        List<PollutionSource> list = sourceManageService.getAbnormalData();
        return Result.success(list);
    }
}
