package com.airquality.service;

import com.airquality.dto.PollutantCreateDTO;
import com.airquality.dto.PollutantManageDTO;
import com.airquality.dto.PollutantUpdateDTO;
import com.airquality.entity.PollutantData;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 污染物管理服务接口
 */
public interface PollutantManageService {

    Page<PollutantData> getPollutantList(PollutantManageDTO dto);
    PollutantData getPollutantById(Long id);
    void addPollutant(PollutantCreateDTO dto);
    void updatePollutant(PollutantUpdateDTO dto);
    void deletePollutant(Long id);
    void batchDeletePollutant(List<Long> ids);
    List<PollutantData> getAbnormalData();
}
