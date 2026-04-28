package com.example.studentmanager.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.entity.CheckinRecord;
import com.example.studentmanager.entity.Repair;
import com.example.studentmanager.entity.Student;
import com.example.studentmanager.exception.BusinessException;
import com.example.studentmanager.mapper.RepairMapper;
import com.example.studentmanager.service.CheckinRecordService;
import com.example.studentmanager.service.RepairService;
import com.example.studentmanager.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RepairServiceImpl extends ServiceImpl<RepairMapper, Repair> implements RepairService {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CheckinRecordService checkinRecordService;

    @Override
    public PageResult<Repair> pageRepair(Integer current, Integer size, Integer repairType, Integer status, Long studentId, Long buildingId) {
        Page<Repair> page = new Page<>(current, size);
        LambdaQueryWrapper<Repair> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Repair::getDeleted, 0);
        if (repairType != null) {
            wrapper.eq(Repair::getRepairType, repairType);
        }
        if (status != null) {
            wrapper.eq(Repair::getStatus, status);
        }
        if (studentId != null) {
            wrapper.eq(Repair::getStudentId, studentId);
        }
        if (buildingId != null) {
            wrapper.eq(Repair::getBuildingId, buildingId);
        }
        wrapper.orderByDesc(Repair::getCreateTime);
        Page<Repair> resultPage = page(page, wrapper);
        
        List<Repair> repairs = baseMapper.selectRepairWithDetail();
        return new PageResult<>(
                resultPage.getTotal(),
                repairs,
                resultPage.getCurrent(),
                resultPage.getSize(),
                resultPage.getPages()
        );
    }

    @Override
    public Repair getRepairWithDetailById(Long id) {
        return baseMapper.selectRepairWithDetailById(id);
    }

    @Override
    public List<Repair> getByStudentId(Long studentId) {
        return baseMapper.selectRepairWithDetailByStudentId(studentId);
    }

    @Override
    public List<Repair> getByDormitoryId(Long dormitoryId) {
        return baseMapper.selectRepairWithDetailByDormitoryId(dormitoryId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRepair(Repair repair) {
        Long studentId = repair.getStudentId();
        if (studentId == null) {
            throw new BusinessException("学生ID不能为空");
        }
        
        Student student = studentService.getById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        
        CheckinRecord checkinRecord = checkinRecordService.getActiveByStudentId(studentId);
        if (checkinRecord == null) {
            throw new BusinessException("该学生没有入住记录，无法报修");
        }
        
        repair.setDormitoryId(checkinRecord.getDormitoryId());
        repair.setBuildingId(checkinRecord.getBuildingId());
        repair.setStatus(1);
        
        return save(repair);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleRepair(Long id, Long handlerId, String handleRemark) {
        Repair repair = getById(id);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }
        
        if (repair.getStatus() == 3) {
            throw new BusinessException("该报修已完成处理");
        }
        
        repair.setStatus(2);
        repair.setHandlerId(handlerId);
        repair.setHandleRemark(handleRemark);
        repair.setHandleTime(LocalDateTime.now());
        
        return updateById(repair);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRepairStatus(Long id, Integer status, Long handlerId, String handleRemark) {
        Repair repair = getById(id);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }
        
        repair.setStatus(status);
        if (handlerId != null) {
            repair.setHandlerId(handlerId);
        }
        if (StrUtil.isNotBlank(handleRemark)) {
            repair.setHandleRemark(handleRemark);
        }
        if (status == 2 || status == 3 || status == 4) {
            repair.setHandleTime(LocalDateTime.now());
        }
        
        return updateById(repair);
    }
}
