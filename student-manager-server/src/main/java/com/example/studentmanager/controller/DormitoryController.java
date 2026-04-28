package com.example.studentmanager.controller;

import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.common.Result;
import com.example.studentmanager.entity.Dormitory;
import com.example.studentmanager.service.DormitoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dormitory")
public class DormitoryController {

    @Autowired
    private DormitoryService dormitoryService;

    @GetMapping("/page")
    public Result<PageResult<Dormitory>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) Integer status) {
        PageResult<Dormitory> result = dormitoryService.pageDormitory(current, size, roomNumber, buildingId, status);
        return Result.success(result);
    }

    @GetMapping("/list/{buildingId}")
    public Result<List<Dormitory>> listByBuildingId(@PathVariable Long buildingId) {
        List<Dormitory> dormitories = dormitoryService.listByBuildingId(buildingId);
        return Result.success(dormitories);
    }

    @GetMapping("/available")
    public Result<List<Dormitory>> listAvailable(@RequestParam(required = false) Long buildingId) {
        List<Dormitory> dormitories = dormitoryService.listAvailable(buildingId);
        return Result.success(dormitories);
    }

    @GetMapping("/{id}")
    public Result<Dormitory> getById(@PathVariable Long id) {
        Dormitory dormitory = dormitoryService.getDormitoryWithBuildingById(id);
        if (dormitory == null) {
            return Result.error("宿舍不存在");
        }
        return Result.success(dormitory);
    }

    @PostMapping
    public Result<Void> save(@RequestBody Dormitory dormitory) {
        boolean success = dormitoryService.saveDormitory(dormitory);
        if (success) {
            return Result.success();
        } else {
            return Result.error("保存失败");
        }
    }

    @PutMapping
    public Result<Void> update(@RequestBody Dormitory dormitory) {
        boolean success = dormitoryService.updateDormitory(dormitory);
        if (success) {
            return Result.success();
        } else {
            return Result.error("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = dormitoryService.deleteDormitory(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        if (status == null) {
            return Result.error("状态不能为空");
        }
        Dormitory dormitory = dormitoryService.getById(id);
        if (dormitory == null) {
            return Result.error("宿舍不存在");
        }
        dormitory.setStatus(status);
        boolean success = dormitoryService.updateById(dormitory);
        if (success) {
            return Result.success();
        } else {
            return Result.error("更新状态失败");
        }
    }
}
