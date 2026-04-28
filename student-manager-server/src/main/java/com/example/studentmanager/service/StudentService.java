package com.example.studentmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.entity.Student;

import java.util.List;

public interface StudentService extends IService<Student> {

    PageResult<Student> pageStudent(Integer current, Integer size, String studentNo, String name, String department, Integer status);

    Student getStudentWithDormitoryById(Long id);

    Student getByStudentNo(String studentNo);

    Student getByUserId(Long userId);

    boolean saveStudent(Student student);

    boolean updateStudent(Student student);

    boolean deleteStudent(Long id);
}
