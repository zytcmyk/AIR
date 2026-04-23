package com.airquality.service.impl;

import com.airquality.dto.AqiCreateDTO;
import com.airquality.dto.AqiManageDTO;
import com.airquality.dto.AqiUpdateDTO;
import com.airquality.entity.AqiData;
import com.airquality.mapper.AqiMapper;
import com.airquality.service.AqiManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * AQI管理服务实现类
 */
@Service
public class AqiManageServiceImpl implements AqiManageService {

    @Resource
    private AqiMapper aqiMapper;

    @Override
    public Page<AqiData> getAqiList(AqiManageDTO dto) {
        Page<AqiData> page = new Page<>(dto.getPage(), dto.getSize());
        QueryWrapper<AqiData> wrapper = new QueryWrapper<>();

        if (dto.getCity() != null && !dto.getCity().isEmpty()) {
            wrapper.like("city", dto.getCity());
        }
        if (dto.getStartDate() != null && !dto.getStartDate().isEmpty()) {
            wrapper.ge("date", dto.getStartDate());
        }
        if (dto.getEndDate() != null && !dto.getEndDate().isEmpty()) {
            wrapper.le("date", dto.getEndDate());
        }
        wrapper.orderByDesc("date");

        return aqiMapper.selectPage(page, wrapper);
    }

    @Override
    public AqiData getAqiById(Long id) {
        return aqiMapper.selectById(id);
    }

    @Override
    public void addAqi(AqiCreateDTO dto) {
        AqiData aqiData = new AqiData();
        BeanUtils.copyProperties(dto, aqiData);

        // 自动计算质量等级
        if (dto.getQualityLevel() == null || dto.getQualityLevel().isEmpty()) {
            aqiData.setQualityLevel(calculateQualityLevel(dto.getAqiValue()));
        }

        aqiMapper.insert(aqiData);
    }

    @Override
    public void updateAqi(AqiUpdateDTO dto) {
        AqiData aqiData = aqiMapper.selectById(dto.getId());
        if (aqiData == null) {
            throw new RuntimeException("数据不存在");
        }

        if (dto.getCity() != null) {
            aqiData.setCity(dto.getCity());
        }
        if (dto.getAqiValue() != null) {
            aqiData.setAqiValue(dto.getAqiValue());
            // 重新计算质量等级
            aqiData.setQualityLevel(calculateQualityLevel(dto.getAqiValue()));
        }
        if (dto.getQualityLevel() != null) {
            aqiData.setQualityLevel(dto.getQualityLevel());
        }
        if (dto.getDate() != null) {
            aqiData.setDate(dto.getDate());
        }

        aqiMapper.updateById(aqiData);
    }

    @Override
    public void deleteAqi(Long id) {
        aqiMapper.deleteById(id);
    }

    @Override
    public void batchDeleteAqi(List<Long> ids) {
        aqiMapper.deleteBatchIds(ids);
    }

    @Override
    public List<AqiData> getAbnormalData() {
        QueryWrapper<AqiData> wrapper = new QueryWrapper<>();
        wrapper.lt("aqi_value", 0).or().gt("aqi_value", 500);
        return aqiMapper.selectList(wrapper);
    }

    /**
     * 根据AQI值计算质量等级
     */
    private String calculateQualityLevel(Integer aqiValue) {
        if (aqiValue <= 50) {
            return "优";
        } else if (aqiValue <= 100) {
            return "良";
        } else if (aqiValue <= 150) {
            return "轻度污染";
        } else if (aqiValue <= 200) {
            return "中度污染";
        } else if (aqiValue <= 300) {
            return "重度污染";
        } else {
            return "严重污染";
        }
    }
}
