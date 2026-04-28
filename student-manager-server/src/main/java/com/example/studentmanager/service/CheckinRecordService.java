package com.example.studentmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.entity.CheckinRecord;

import java.time.LocalDate;
import java.util.List;

public interface CheckinRecordService extends IService<CheckinRecord> {

    PageResult<CheckinRecord> pageRecord(Integer current, Integer size, String studentNo, String studentName, Long buildingId, Integer status, LocalDate startDate, LocalDate endDate);

    CheckinRecord getRecordWithDetailById(Long id);

    List<CheckinRecord> getByStudentId(Long studentId);

    List<CheckinRecord> getByDormitoryId(Long dormitoryId);

    CheckinRecord getActiveByStudentId(Long studentId);

    boolean checkin(CheckinRecord record);

    boolean checkout(Long id, String remark);
}
