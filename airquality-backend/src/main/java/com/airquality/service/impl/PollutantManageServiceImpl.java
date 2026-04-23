package com.airquality.service.impl;

import com.airquality.dto.PollutantCreateDTO;
import com.airquality.dto.PollutantManageDTO;
import com.airquality.dto.PollutantUpdateDTO;
import com.airquality.entity.PollutantData;
import com.airquality.mapper.PollutantMapper;
import com.airquality.service.PollutantManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 污染物管理服务实现类
 */
@Service
public class PollutantManageServiceImpl implements PollutantManageService {

    @Resource
    private PollutantMapper pollutantMapper;

    // 污染物合理范围
    private static final Map<String, double[]> POLLUTANT_RANGES = new HashMap<String, double[]>() {{
        put("PM2.5", new double[]{0, 500});
        put("PM10", new double[]{0, 600});
        put("SO2", new double[]{0, 800});
        put("NO2", new double[]{0, 400});
        put("O3", new double[]{0, 400});
        put("CO", new double[]{0, 20});
    }};

    @Override
    public Page<PollutantData> getPollutantList(PollutantManageDTO dto) {
        Page<PollutantData> page = new Page<>(dto.getPage(), dto.getSize());
        QueryWrapper<PollutantData> wrapper = new QueryWrapper<>();

        if (dto.getCity() != null && !dto.getCity().isEmpty()) {
            wrapper.like("city", dto.getCity());
        }
        if (dto.getPollutantType() != null && !dto.getPollutantType().isEmpty()) {
            wrapper.eq("pollutant_type", dto.getPollutantType());
        }
        if (dto.getStartDate() != null && !dto.getStartDate().isEmpty()) {
            wrapper.ge("date", dto.getStartDate());
        }
        if (dto.getEndDate() != null && !dto.getEndDate().isEmpty()) {
            wrapper.le("date", dto.getEndDate());
        }

        wrapper.orderByDesc("date");

        return pollutantMapper.selectPage(page, wrapper);
    }

    @Override
    public PollutantData getPollutantById(Long id) {
        return pollutantMapper.selectById(id);
    }

    @Override
    public void addPollutant(PollutantCreateDTO dto) {
        PollutantData data = new PollutantData();
        BeanUtils.copyProperties(dto, data);
        // 自动设置单位
        if (dto.getUnit() == null || dto.getUnit().isEmpty()) {
            data.setUnit(getUnitByType(dto.getPollutantType()));
        }
        pollutantMapper.insert(data);
    }

    @Override
    public void updatePollutant(PollutantUpdateDTO dto) {
        PollutantData data = pollutantMapper.selectById(dto.getId());
        if (data == null) {
            throw new RuntimeException("数据不存在");
        }
        if (dto.getCity() != null) data.setCity(dto.getCity());
        if (dto.getPollutantType() != null) data.setPollutantType(dto.getPollutantType());
        if (dto.getConcentration() != null) data.setConcentration(dto.getConcentration());
        if (dto.getUnit() != null) data.setUnit(dto.getUnit());
        if (dto.getDate() != null) data.setDate(dto.getDate());
        pollutantMapper.updateById(data);
    }

    @Override
    public void deletePollutant(Long id) {
        pollutantMapper.deleteById(id);
    }

    @Override
    public void batchDeletePollutant(List<Long> ids) {
        pollutantMapper.deleteBatchIds(ids);
    }

    @Override
    public List<PollutantData> getAbnormalData() {
        QueryWrapper<PollutantData> wrapper = new QueryWrapper<>();
        wrapper.lt("value", 0);
        return pollutantMapper.selectList(wrapper);
    }

    private String getUnitByType(String type) {
        if ("CO".equals(type)) {
            return "mg/m³";
        }
        return "μg/m³";
    }
}
