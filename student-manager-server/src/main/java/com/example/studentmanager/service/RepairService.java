package com.example.studentmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.entity.Repair;

import java.util.List;

public interface RepairService extends IService<Repair> {

    PageResult<Repair> pageRepair(Integer current, Integer size, Integer repairType, Integer status, Long studentId, Long buildingId);

    Repair getRepairWithDetailById(Long id);

    List<Repair> getByStudentId(Long studentId);

    List<Repair> getByDormitoryId(Long dormitoryId);

    boolean createRepair(Repair repair);

    boolean handleRepair(Long id, Long handlerId, String handleRemark);

    boolean updateRepairStatus(Long id, Integer status, Long handlerId, String handleRemark);
}
