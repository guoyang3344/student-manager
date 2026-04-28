package com.example.studentmanager.controller;

import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.common.Result;
import com.example.studentmanager.entity.DormitoryBuilding;
import com.example.studentmanager.service.DormitoryBuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/building")
public class DormitoryBuildingController {

    @Autowired
    private DormitoryBuildingService buildingService;

    @GetMapping("/page")
    public Result<PageResult<DormitoryBuilding>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String buildingName,
            @RequestParam(required = false) Integer genderType,
            @RequestParam(required = false) Integer status) {
        PageResult<DormitoryBuilding> result = buildingService.pageBuilding(current, size, buildingName, genderType, status);
        return Result.success(result);
    }

    @GetMapping("/list")
    public Result<List<DormitoryBuilding>> list() {
        List<DormitoryBuilding> buildings = buildingService.listAll();
        return Result.success(buildings);
    }

    @GetMapping("/{id}")
    public Result<DormitoryBuilding> getById(@PathVariable Long id) {
        DormitoryBuilding building = buildingService.getById(id);
        if (building == null) {
            return Result.error("宿舍楼不存在");
        }
        return Result.success(building);
    }

    @PostMapping
    public Result<Void> save(@RequestBody DormitoryBuilding building) {
        boolean success = buildingService.saveBuilding(building);
        if (success) {
            return Result.success();
        } else {
            return Result.error("保存失败");
        }
    }

    @PutMapping
    public Result<Void> update(@RequestBody DormitoryBuilding building) {
        boolean success = buildingService.updateBuilding(building);
        if (success) {
            return Result.success();
        } else {
            return Result.error("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = buildingService.deleteBuilding(id);
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
        DormitoryBuilding building = buildingService.getById(id);
        if (building == null) {
            return Result.error("宿舍楼不存在");
        }
        building.setStatus(status);
        boolean success = buildingService.updateById(building);
        if (success) {
            return Result.success();
        } else {
            return Result.error("更新状态失败");
        }
    }
}
