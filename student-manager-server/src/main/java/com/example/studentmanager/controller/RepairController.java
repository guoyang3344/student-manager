package com.example.studentmanager.controller;

import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.common.Result;
import com.example.studentmanager.entity.Repair;
import com.example.studentmanager.entity.Student;
import com.example.studentmanager.service.RepairService;
import com.example.studentmanager.service.StudentService;
import com.example.studentmanager.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/repair")
public class RepairController {

    @Autowired
    private RepairService repairService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/page")
    public Result<PageResult<Repair>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer repairType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long buildingId) {
        PageResult<Repair> result = repairService.pageRepair(current, size, repairType, status, studentId, buildingId);
        return Result.success(result);
    }

    @GetMapping("/my")
    public Result<List<Repair>> getMyRepairs(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        List<Repair> repairs = repairService.list();
        return Result.success(repairs);
    }

    @GetMapping("/{id}")
    public Result<Repair> getById(@PathVariable Long id) {
        Repair repair = repairService.getRepairWithDetailById(id);
        if (repair == null) {
            return Result.error("报修记录不存在");
        }
        return Result.success(repair);
    }

    @GetMapping("/student/{studentId}")
    public Result<List<Repair>> getByStudentId(@PathVariable Long studentId) {
        List<Repair> repairs = repairService.getByStudentId(studentId);
        return Result.success(repairs);
    }

    @PostMapping
    public Result<Void> createRepair(@RequestBody Repair repair, HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        repair.setUserId(userId);
        
        if (repair.getStudentId() == null) {
            Student student = studentService.getByUserId(userId);
            if (student != null) {
                repair.setStudentId(student.getId());
            }
        }
        
        boolean success = repairService.createRepair(repair);
        if (success) {
            return Result.successMessage("报修提交成功");
        } else {
            return Result.error("报修提交失败");
        }
    }

    @PutMapping("/handle/{id}")
    public Result<Void> handleRepair(@PathVariable Long id, @RequestBody Map<String, String> params, HttpServletRequest request) {
        Long handlerId = getUserIdFromToken(request);
        String handleRemark = params.get("handleRemark");
        
        boolean success = repairService.handleRepair(id, handlerId, handleRemark);
        if (success) {
            return Result.successMessage("处理成功");
        } else {
            return Result.error("处理失败");
        }
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Object> params, HttpServletRequest request) {
        Integer status = (Integer) params.get("status");
        String handleRemark = (String) params.get("handleRemark");
        Long handlerId = getUserIdFromToken(request);
        
        if (status == null) {
            return Result.error("状态不能为空");
        }
        
        boolean success = repairService.updateRepairStatus(id, status, handlerId, handleRemark);
        if (success) {
            return Result.successMessage("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    private Long getUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtUtil.validateToken(token)) {
                return jwtUtil.getUserId(token);
            }
        }
        return null;
    }
}
