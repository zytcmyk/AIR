package com.airquality.service;

import com.airquality.dto.AqiCreateDTO;
import com.airquality.dto.AqiManageDTO;
import com.airquality.dto.AqiUpdateDTO;
import com.airquality.entity.AqiData;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * AQI管理服务接口
 */
public interface AqiManageService {

    /**
     * 分页查询AQI数据
     */
    Page<AqiData> getAqiList(AqiManageDTO dto);

    /**
     * 获取AQI数据详情
     */
    AqiData getAqiById(Long id);

    /**
     * 添加AQI数据
     */
    void addAqi(AqiCreateDTO dto);

    /**
     * 更新AQI数据
     */
    void updateAqi(AqiUpdateDTO dto);

    /**
     * 删除AQI数据
     */
    void deleteAqi(Long id);

    /**
     * 批量删除AQI数据
     */
    void batchDeleteAqi(List<Long> ids);

    /**
     * 查询异常数据
     */
    List<AqiData> getAbnormalData();
}
