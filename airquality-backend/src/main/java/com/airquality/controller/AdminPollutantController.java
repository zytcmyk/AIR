package com.airquality.controller;

import com.airquality.annotation.AdminRequired;
import com.airquality.dto.PollutantCreateDTO;
import com.airquality.dto.PollutantManageDTO;
import com.airquality.dto.PollutantUpdateDTO;
import com.airquality.entity.PollutantData;
import com.airquality.service.PollutantManageService;
import com.airquality.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 污染物管理控制器（管理员）
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/pollutant")
@AdminRequired
public class AdminPollutantController {

    @Resource
    private PollutantManageService pollutantManageService;

    @GetMapping("/list")
    public Result<Map<String, Object>> getPollutantList(PollutantManageDTO dto) {
        Page<PollutantData> page = pollutantManageService.getPollutantList(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("list", page.getRecords());
        result.put("total", page.getTotal());
        result.put("page", page.getCurrent());
        result.put("size", page.getSize());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<PollutantData> getPollutantById(@PathVariable Long id) {
        PollutantData data = pollutantManageService.getPollutantById(id);
        return Result.success(data);
    }

    @PostMapping
    public Result<Void> addPollutant(@RequestBody PollutantCreateDTO dto) {
        try {
            pollutantManageService.addPollutant(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> updatePollutant(@PathVariable Long id, @RequestBody PollutantUpdateDTO dto) {
        dto.setId(id);
        try {
            pollutantManageService.updatePollutant(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePollutant(@PathVariable Long id) {
        pollutantManageService.deletePollutant(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> batchDeletePollutant(@RequestBody List<Long> ids) {
        pollutantManageService.batchDeletePollutant(ids);
        return Result.success();
    }

    @GetMapping("/abnormal")
    public Result<List<PollutantData>> getAbnormalData() {
        List<PollutantData> list = pollutantManageService.getAbnormalData();
        return Result.success(list);
    }
}
