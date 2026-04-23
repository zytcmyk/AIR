package com.airquality.service.impl;

import com.airquality.dto.SourceCreateDTO;
import com.airquality.dto.SourceManageDTO;
import com.airquality.dto.SourceUpdateDTO;
import com.airquality.entity.PollutionSource;
import com.airquality.mapper.PollutionSourceMapper;
import com.airquality.service.SourceManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 污染源管理服务实现类
 */
@Service
public class SourceManageServiceImpl implements SourceManageService {

    @Resource
    private PollutionSourceMapper pollutionSourceMapper;

    @Override
    public Page<PollutionSource> getSourceList(SourceManageDTO dto) {
        Page<PollutionSource> page = new Page<>(dto.getPage(), dto.getSize());
        QueryWrapper<PollutionSource> wrapper = new QueryWrapper<>();

        if (dto.getCity() != null && !dto.getCity().isEmpty()) {
            wrapper.like("city", dto.getCity());
        }
        if (dto.getSourceType() != null && !dto.getSourceType().isEmpty()) {
            wrapper.eq("source_type", dto.getSourceType());
        }
        if (dto.getStartDate() != null && !dto.getStartDate().isEmpty()) {
            wrapper.ge("date", dto.getStartDate());
        }
        if (dto.getEndDate() != null && !dto.getEndDate().isEmpty()) {
            wrapper.le("date", dto.getEndDate());
        }
        wrapper.orderByDesc("date");

        return pollutionSourceMapper.selectPage(page, wrapper);
    }

    @Override
    public PollutionSource getSourceById(Long id) {
        return pollutionSourceMapper.selectById(id);
    }

    @Override
    public void addSource(SourceCreateDTO dto) {
        // 验证贡献率范围
        if (dto.getContributionRate().compareTo(BigDecimal.ZERO) < 0 ||
            dto.getContributionRate().compareTo(new BigDecimal("100")) > 0) {
            throw new RuntimeException("贡献率必须在0-100之间");
        }

        PollutionSource data = new PollutionSource();
        BeanUtils.copyProperties(dto, data);
        pollutionSourceMapper.insert(data);
    }

    @Override
    public void updateSource(SourceUpdateDTO dto) {
        PollutionSource data = pollutionSourceMapper.selectById(dto.getId());
        if (data == null) {
            throw new RuntimeException("数据不存在");
        }

        if (dto.getCity() != null) data.setCity(dto.getCity());
        if (dto.getSourceType() != null) data.setSourceType(dto.getSourceType());
        if (dto.getContributionRate() != null) {
            if (dto.getContributionRate().compareTo(BigDecimal.ZERO) < 0 ||
                dto.getContributionRate().compareTo(new BigDecimal("100")) > 0) {
                throw new RuntimeException("贡献率必须在0-100之间");
            }
            data.setContributionRate(dto.getContributionRate());
        }
        if (dto.getDate() != null) data.setDate(dto.getDate());

        pollutionSourceMapper.updateById(data);
    }

    @Override
    public void deleteSource(Long id) {
        pollutionSourceMapper.deleteById(id);
    }

    @Override
    public void batchDeleteSource(List<Long> ids) {
        pollutionSourceMapper.deleteBatchIds(ids);
    }

    @Override
    public List<PollutionSource> getAbnormalData() {
        QueryWrapper<PollutionSource> wrapper = new QueryWrapper<>();
        wrapper.lt("contribution_rate", 0).or().gt("contribution_rate", 100);
        return pollutionSourceMapper.selectList(wrapper);
    }
}
