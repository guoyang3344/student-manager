package com.example.studentmanager.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.entity.CheckinRecord;
import com.example.studentmanager.entity.Student;
import com.example.studentmanager.exception.BusinessException;
import com.example.studentmanager.mapper.CheckinRecordMapper;
import com.example.studentmanager.mapper.StudentMapper;
import com.example.studentmanager.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Autowired
    private CheckinRecordMapper checkinRecordMapper;

    @Override
    public PageResult<Student> pageStudent(Integer current, Integer size, String studentNo, String name, String department, Integer status) {
        Page<Student> page = new Page<>(current, size);
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getDeleted, 0);
        if (StrUtil.isNotBlank(studentNo)) {
            wrapper.like(Student::getStudentNo, studentNo);
        }
        if (StrUtil.isNotBlank(name)) {
            wrapper.like(Student::getName, name);
        }
        if (StrUtil.isNotBlank(department)) {
            wrapper.like(Student::getDepartment, department);
        }
        if (status != null) {
            wrapper.eq(Student::getStatus, status);
        }
        wrapper.orderByDesc(Student::getCreateTime);
        Page<Student> resultPage = page(page, wrapper);
        
        List<Student> students = baseMapper.selectStudentWithDormitory();
        return new PageResult<>(
                resultPage.getTotal(),
                students,
                resultPage.getCurrent(),
                resultPage.getSize(),
                resultPage.getPages()
        );
    }

    @Override
    public Student getStudentWithDormitoryById(Long id) {
        return baseMapper.selectStudentWithDormitoryById(id);
    }

    @Override
    public Student getByStudentNo(String studentNo) {
        return getOne(new LambdaQueryWrapper<Student>()
                .eq(Student::getStudentNo, studentNo)
                .eq(Student::getDeleted, 0));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveStudent(Student student) {
        Student existStudent = getByStudentNo(student.getStudentNo());
        if (existStudent != null) {
            throw new BusinessException("学号已存在");
        }
        return save(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStudent(Student student) {
        Student existStudent = getById(student.getId());
        if (existStudent == null) {
            throw new BusinessException("学生不存在");
        }
        Student sameNoStudent = getByStudentNo(student.getStudentNo());
        if (sameNoStudent != null && !sameNoStudent.getId().equals(student.getId())) {
            throw new BusinessException("学号已存在");
        }
        return updateById(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteStudent(Long id) {
        Student student = getById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        List<CheckinRecord> records = checkinRecordMapper.selectCheckinRecordWithDetailByStudentId(id);
        if (records != null && !records.isEmpty()) {
            for (CheckinRecord record : records) {
                if (record.getStatus() == 1) {
                    throw new BusinessException("该学生还有未退房记录，请先办理退房");
                }
            }
        }
        return removeById(id);
    }
}
