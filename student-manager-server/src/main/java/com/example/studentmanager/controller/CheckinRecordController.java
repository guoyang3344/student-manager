package com.example.studentmanager.controller;

import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.common.Result;
import com.example.studentmanager.entity.CheckinRecord;
import com.example.studentmanager.service.CheckinRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkin")
public class CheckinRecordController {

    @Autowired
    private CheckinRecordService checkinRecordService;

    @GetMapping("/page")
    public Result<PageResult<CheckinRecord>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        PageResult<CheckinRecord> result = checkinRecordService.pageRecord(current, size, studentNo, studentName, buildingId, status, startDate, endDate);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<CheckinRecord> getById(@PathVariable Long id) {
        CheckinRecord record = checkinRecordService.getRecordWithDetailById(id);
        if (record == null) {
            return Result.error("入住记录不存在");
        }
        return Result.success(record);
    }

    @GetMapping("/student/{studentId}")
    public Result<List<CheckinRecord>> getByStudentId(@PathVariable Long studentId) {
        List<CheckinRecord> records = checkinRecordService.getByStudentId(studentId);
        return Result.success(records);
    }

    @GetMapping("/active/{studentId}")
    public Result<CheckinRecord> getActiveByStudentId(@PathVariable Long studentId) {
        CheckinRecord record = checkinRecordService.getActiveByStudentId(studentId);
        return Result.success(record);
    }

    @PostMapping
    public Result<Void> checkin(@RequestBody CheckinRecord record) {
        boolean success = checkinRecordService.checkin(record);
        if (success) {
            return Result.successMessage("入住办理成功");
        } else {
            return Result.error("入住办理失败");
        }
    }

    @PutMapping("/checkout/{id}")
    public Result<Void> checkout(@PathVariable Long id, @RequestBody(required = false) Map<String, String> params) {
        String remark = params != null ? params.get("remark") : null;
        boolean success = checkinRecordService.checkout(id, remark);
        if (success) {
            return Result.successMessage("退房办理成功");
        } else {
            return Result.error("退房办理失败");
        }
    }
}
