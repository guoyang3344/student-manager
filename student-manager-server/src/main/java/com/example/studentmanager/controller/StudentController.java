package com.example.studentmanager.controller;

import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.common.Result;
import com.example.studentmanager.entity.Student;
import com.example.studentmanager.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/page")
    public Result<PageResult<Student>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer status) {
        PageResult<Student> result = studentService.pageStudent(current, size, studentNo, name, department, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Long id) {
        Student student = studentService.getStudentWithDormitoryById(id);
        if (student == null) {
            return Result.error("学生不存在");
        }
        return Result.success(student);
    }

    @PostMapping
    public Result<Void> save(@RequestBody Student student) {
        boolean success = studentService.saveStudent(student);
        if (success) {
            return Result.success();
        } else {
            return Result.error("保存失败");
        }
    }

    @PutMapping
    public Result<Void> update(@RequestBody Student student) {
        boolean success = studentService.updateStudent(student);
        if (success) {
            return Result.success();
        } else {
            return Result.error("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = studentService.deleteStudent(id);
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
        Student student = studentService.getById(id);
        if (student == null) {
            return Result.error("学生不存在");
        }
        student.setStatus(status);
        boolean success = studentService.updateById(student);
        if (success) {
            return Result.success();
        } else {
            return Result.error("更新状态失败");
        }
    }
}
