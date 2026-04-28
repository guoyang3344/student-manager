package com.example.studentmanager.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.entity.DormitoryBuilding;
import com.example.studentmanager.entity.Dormitory;
import com.example.studentmanager.exception.BusinessException;
import com.example.studentmanager.mapper.DormitoryBuildingMapper;
import com.example.studentmanager.mapper.DormitoryMapper;
import com.example.studentmanager.service.DormitoryBuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DormitoryBuildingServiceImpl extends ServiceImpl<DormitoryBuildingMapper, DormitoryBuilding> implements DormitoryBuildingService {

    @Autowired
    private DormitoryMapper dormitoryMapper;

    @Override
    public PageResult<DormitoryBuilding> pageBuilding(Integer current, Integer size, String buildingName, Integer genderType, Integer status) {
        Page<DormitoryBuilding> page = new Page<>(current, size);
        LambdaQueryWrapper<DormitoryBuilding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DormitoryBuilding::getDeleted, 0);
        if (StrUtil.isNotBlank(buildingName)) {
            wrapper.like(DormitoryBuilding::getBuildingName, buildingName)
                    .or().like(DormitoryBuilding::getBuildingNumber, buildingName);
        }
        if (genderType != null) {
            wrapper.eq(DormitoryBuilding::getGenderType, genderType);
        }
        if (status != null) {
            wrapper.eq(DormitoryBuilding::getStatus, status);
        }
        wrapper.orderByAsc(DormitoryBuilding::getBuildingNumber);
        Page<DormitoryBuilding> resultPage = page(page, wrapper);
        return new PageResult<>(
                resultPage.getTotal(),
                resultPage.getRecords(),
                resultPage.getCurrent(),
                resultPage.getSize(),
                resultPage.getPages()
        );
    }

    @Override
    public List<DormitoryBuilding> listAll() {
        return list(new LambdaQueryWrapper<DormitoryBuilding>()
                .eq(DormitoryBuilding::getDeleted, 0)
                .eq(DormitoryBuilding::getStatus, 1)
                .orderByAsc(DormitoryBuilding::getBuildingNumber));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBuilding(DormitoryBuilding building) {
        LambdaQueryWrapper<DormitoryBuilding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DormitoryBuilding::getBuildingNumber, building.getBuildingNumber())
                .eq(DormitoryBuilding::getDeleted, 0);
        DormitoryBuilding existBuilding = getOne(wrapper);
        if (existBuilding != null) {
            throw new BusinessException("宿舍楼编号已存在");
        }
        return save(building);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBuilding(DormitoryBuilding building) {
        DormitoryBuilding existBuilding = getById(building.getId());
        if (existBuilding == null) {
            throw new BusinessException("宿舍楼不存在");
        }
        LambdaQueryWrapper<DormitoryBuilding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DormitoryBuilding::getBuildingNumber, building.getBuildingNumber())
                .eq(DormitoryBuilding::getDeleted, 0);
        DormitoryBuilding sameNumberBuilding = getOne(wrapper);
        if (sameNumberBuilding != null && !sameNumberBuilding.getId().equals(building.getId())) {
            throw new BusinessException("宿舍楼编号已存在");
        }
        return updateById(building);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBuilding(Long id) {
        DormitoryBuilding building = getById(id);
        if (building == null) {
            throw new BusinessException("宿舍楼不存在");
        }
        List<Dormitory> dormitories = dormitoryMapper.selectDormitoryWithBuildingByBuildingId(id);
        if (dormitories != null && !dormitories.isEmpty()) {
            throw new BusinessException("该宿舍楼下还有宿舍，请先删除宿舍");
        }
        return removeById(id);
    }
}
