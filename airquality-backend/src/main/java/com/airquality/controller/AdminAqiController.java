package com.airquality.controller;

import com.airquality.annotation.AdminRequired;
import com.airquality.dto.AqiCreateDTO;
import com.airquality.dto.AqiManageDTO;
import com.airquality.dto.AqiUpdateDTO;
import com.airquality.entity.AqiData;
import com.airquality.service.AqiManageService;
import com.airquality.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AQI管理控制器（管理员）
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/aqi")
@AdminRequired
public class AdminAqiController {

    @Resource
    private AqiManageService aqiManageService;

    /**
     * 分页查询AQI数据
     * GET /api/admin/aqi/list
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getAqiList(AqiManageDTO dto) {
        Page<AqiData> page = aqiManageService.getAqiList(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("list", page.getRecords());
        result.put("total", page.getTotal());
        result.put("page", page.getCurrent());
        result.put("size", page.getSize());
        return Result.success(result);
    }

    /**
     * 获取AQI数据详情
     * GET /api/admin/aqi/{id}
     */
    @GetMapping("/{id}")
    public Result<AqiData> getAqiById(@PathVariable Long id) {
        AqiData aqiData = aqiManageService.getAqiById(id);
        return Result.success(aqiData);
    }

    /**
     * 添加AQI数据
     * POST /api/admin/aqi
     */
    @PostMapping
    public Result<Void> addAqi(@RequestBody AqiCreateDTO dto) {
        try {
            aqiManageService.addAqi(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 更新AQI数据
     * PUT /api/admin/aqi/{id}
     */
    @PutMapping("/{id}")
    public Result<Void> updateAqi(@PathVariable Long id, @RequestBody AqiUpdateDTO dto) {
        dto.setId(id);
        try {
            aqiManageService.updateAqi(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 删除AQI数据
     * DELETE /api/admin/aqi/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAqi(@PathVariable Long id) {
        aqiManageService.deleteAqi(id);
        return Result.success();
    }

    /**
     * 批量删除AQI数据
     * DELETE /api/admin/aqi/batch
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteAqi(@RequestBody List<Long> ids) {
        aqiManageService.batchDeleteAqi(ids);
        return Result.success();
    }

    /**
     * 查询异常数据
     * GET /api/admin/aqi/abnormal
     */
    @GetMapping("/abnormal")
    public Result<List<AqiData>> getAbnormalData() {
        List<AqiData> list = aqiManageService.getAbnormalData();
        return Result.success(list);
    }
}
