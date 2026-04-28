package com.example.studentmanager.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.entity.CheckinRecord;
import com.example.studentmanager.entity.Dormitory;
import com.example.studentmanager.exception.BusinessException;
import com.example.studentmanager.mapper.CheckinRecordMapper;
import com.example.studentmanager.mapper.DormitoryMapper;
import com.example.studentmanager.service.DormitoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DormitoryServiceImpl extends ServiceImpl<DormitoryMapper, Dormitory> implements DormitoryService {

    @Autowired
    private CheckinRecordMapper checkinRecordMapper;

    @Override
    public PageResult<Dormitory> pageDormitory(Integer current, Integer size, String roomNumber, Long buildingId, Integer status) {
        Page<Dormitory> page = new Page<>(current, size);
        LambdaQueryWrapper<Dormitory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dormitory::getDeleted, 0);
        if (StrUtil.isNotBlank(roomNumber)) {
            wrapper.like(Dormitory::getRoomNumber, roomNumber);
        }
        if (buildingId != null) {
            wrapper.eq(Dormitory::getBuildingId, buildingId);
        }
        if (status != null) {
            wrapper.eq(Dormitory::getStatus, status);
        }
        wrapper.orderByAsc(Dormitory::getRoomNumber);
        Page<Dormitory> resultPage = page(page, wrapper);
        
        List<Dormitory> dormitories = baseMapper.selectDormitoryWithBuilding();
        return new PageResult<>(
                resultPage.getTotal(),
                dormitories,
                resultPage.getCurrent(),
                resultPage.getSize(),
                resultPage.getPages()
        );
    }

    @Override
    public Dormitory getDormitoryWithBuildingById(Long id) {
        return baseMapper.selectDormitoryWithBuildingById(id);
    }

    @Override
    public List<Dormitory> listByBuildingId(Long buildingId) {
        return baseMapper.selectDormitoryWithBuildingByBuildingId(buildingId);
    }

    @Override
    public List<Dormitory> listAvailable(Long buildingId) {
        LambdaQueryWrapper<Dormitory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dormitory::getDeleted, 0)
                .eq(Dormitory::getStatus, 1)
                .apply("occupied_beds < bed_count");
        if (buildingId != null) {
            wrapper.eq(Dormitory::getBuildingId, buildingId);
        }
        wrapper.orderByAsc(Dormitory::getRoomNumber);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDormitory(Dormitory dormitory) {
        LambdaQueryWrapper<Dormitory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dormitory::getBuildingId, dormitory.getBuildingId())
                .eq(Dormitory::getRoomNumber, dormitory.getRoomNumber())
                .eq(Dormitory::getDeleted, 0);
        Dormitory existDormitory = getOne(wrapper);
        if (existDormitory != null) {
            throw new BusinessException("该宿舍楼下已存在此房间号");
        }
        dormitory.setOccupiedBeds(0);
        return save(dormitory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDormitory(Dormitory dormitory) {
        Dormitory existDormitory = getById(dormitory.getId());
        if (existDormitory == null) {
            throw new BusinessException("宿舍不存在");
        }
        LambdaQueryWrapper<Dormitory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dormitory::getBuildingId, dormitory.getBuildingId())
                .eq(Dormitory::getRoomNumber, dormitory.getRoomNumber())
                .eq(Dormitory::getDeleted, 0);
        Dormitory sameRoomDormitory = getOne(wrapper);
        if (sameRoomDormitory != null && !sameRoomDormitory.getId().equals(dormitory.getId())) {
            throw new BusinessException("该宿舍楼下已存在此房间号");
        }
        return updateById(dormitory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDormitory(Long id) {
        Dormitory dormitory = getById(id);
        if (dormitory == null) {
            throw new BusinessException("宿舍不存在");
        }
        if (dormitory.getOccupiedBeds() > 0) {
            throw new BusinessException("该宿舍还有学生入住，请先办理退房");
        }
        return removeById(id);
    }
}
