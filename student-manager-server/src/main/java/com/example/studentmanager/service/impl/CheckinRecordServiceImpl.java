package com.example.studentmanager.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.entity.CheckinRecord;
import com.example.studentmanager.entity.Dormitory;
import com.example.studentmanager.entity.Student;
import com.example.studentmanager.exception.BusinessException;
import com.example.studentmanager.mapper.CheckinRecordMapper;
import com.example.studentmanager.mapper.DormitoryMapper;
import com.example.studentmanager.mapper.StudentMapper;
import com.example.studentmanager.service.CheckinRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CheckinRecordServiceImpl extends ServiceImpl<CheckinRecordMapper, CheckinRecord> implements CheckinRecordService {

    @Autowired
    private DormitoryMapper dormitoryMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public PageResult<CheckinRecord> pageRecord(Integer current, Integer size, String studentNo, String studentName, Long buildingId, Integer status, LocalDate startDate, LocalDate endDate) {
        Page<CheckinRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<CheckinRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinRecord::getDeleted, 0);
        if (buildingId != null) {
            wrapper.eq(CheckinRecord::getBuildingId, buildingId);
        }
        if (status != null) {
            wrapper.eq(CheckinRecord::getStatus, status);
        }
        if (startDate != null) {
            wrapper.ge(CheckinRecord::getCheckinDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(CheckinRecord::getCheckinDate, endDate);
        }
        wrapper.orderByDesc(CheckinRecord::getCreateTime);
        Page<CheckinRecord> resultPage = page(page, wrapper);
        
        List<CheckinRecord> records = baseMapper.selectCheckinRecordWithDetail();
        return new PageResult<>(
                resultPage.getTotal(),
                records,
                resultPage.getCurrent(),
                resultPage.getSize(),
                resultPage.getPages()
        );
    }

    @Override
    public CheckinRecord getRecordWithDetailById(Long id) {
        return baseMapper.selectCheckinRecordWithDetailById(id);
    }

    @Override
    public List<CheckinRecord> getByStudentId(Long studentId) {
        return baseMapper.selectCheckinRecordWithDetailByStudentId(studentId);
    }

    @Override
    public List<CheckinRecord> getByDormitoryId(Long dormitoryId) {
        return baseMapper.selectCheckinRecordWithDetailByDormitoryId(dormitoryId);
    }

    @Override
    public CheckinRecord getActiveByStudentId(Long studentId) {
        return getOne(new LambdaQueryWrapper<CheckinRecord>()
                .eq(CheckinRecord::getStudentId, studentId)
                .eq(CheckinRecord::getStatus, 1)
                .eq(CheckinRecord::getDeleted, 0));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkin(CheckinRecord record) {
        Student student = studentMapper.selectById(record.getStudentId());
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        CheckinRecord activeRecord = getActiveByStudentId(record.getStudentId());
        if (activeRecord != null) {
            throw new BusinessException("该学生已有未退房的入住记录");
        }
        Dormitory dormitory = dormitoryMapper.selectDormitoryWithBuildingById(record.getDormitoryId());
        if (dormitory == null) {
            throw new BusinessException("宿舍不存在");
        }
        if (dormitory.getOccupiedBeds() >= dormitory.getBedCount()) {
            throw new BusinessException("该宿舍床位已满");
        }
        record.setBuildingId(dormitory.getBuildingId());
        record.setStatus(1);
        record.setCheckinDate(LocalDate.now());
        boolean saved = save(record);
        if (saved) {
            dormitory.setOccupiedBeds(dormitory.getOccupiedBeds() + 1);
            dormitoryMapper.updateById(dormitory);
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkout(Long id, String remark) {
        CheckinRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("入住记录不存在");
        }
        if (record.getStatus() == 0) {
            throw new BusinessException("该入住记录已退房");
        }
        record.setStatus(0);
        record.setCheckoutDate(LocalDate.now());
        if (StrUtil.isNotBlank(remark)) {
            record.setRemark(remark);
        }
        boolean updated = updateById(record);
        if (updated) {
            Dormitory dormitory = dormitoryMapper.selectById(record.getDormitoryId());
            if (dormitory != null && dormitory.getOccupiedBeds() > 0) {
                dormitory.setOccupiedBeds(dormitory.getOccupiedBeds() - 1);
                dormitoryMapper.updateById(dormitory);
            }
        }
        return updated;
    }
}
