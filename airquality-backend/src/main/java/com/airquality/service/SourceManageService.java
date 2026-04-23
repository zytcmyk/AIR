package com.airquality.service;

import com.airquality.dto.SourceCreateDTO;
import com.airquality.dto.SourceManageDTO;
import com.airquality.dto.SourceUpdateDTO;
import com.airquality.entity.PollutionSource;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 污染源管理服务接口
 */
public interface SourceManageService {

    Page<PollutionSource> getSourceList(SourceManageDTO dto);
    PollutionSource getSourceById(Long id);
    void addSource(SourceCreateDTO dto);
    void updateSource(SourceUpdateDTO dto);
    void deleteSource(Long id);
    void batchDeleteSource(List<Long> ids);
    List<PollutionSource> getAbnormalData();
}
