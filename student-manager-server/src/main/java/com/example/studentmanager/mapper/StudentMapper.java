package com.example.studentmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentmanager.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    @Select("SELECT s.*, cr.status as checkin_status, d.room_number as dormitory_room_number, db.building_name " +
            "FROM student s " +
            "LEFT JOIN checkin_record cr ON s.id = cr.student_id AND cr.status = 1 AND cr.deleted = 0 " +
            "LEFT JOIN dormitory d ON cr.dormitory_id = d.id AND d.deleted = 0 " +
            "LEFT JOIN dormitory_building db ON d.building_id = db.id AND db.deleted = 0 " +
            "WHERE s.deleted = 0")
    List<Student> selectStudentWithDormitory();

    @Select("SELECT s.*, cr.status as checkin_status, d.room_number as dormitory_room_number, db.building_name " +
            "FROM student s " +
            "LEFT JOIN checkin_record cr ON s.id = cr.student_id AND cr.status = 1 AND cr.deleted = 0 " +
            "LEFT JOIN dormitory d ON cr.dormitory_id = d.id AND d.deleted = 0 " +
            "LEFT JOIN dormitory_building db ON d.building_id = db.id AND db.deleted = 0 " +
            "WHERE s.id = #{id} AND s.deleted = 0")
    Student selectStudentWithDormitoryById(@Param("id") Long id);

    @Select("SELECT s.*, cr.status as checkin_status, d.room_number as dormitory_room_number, db.building_name " +
            "FROM student s " +
            "LEFT JOIN checkin_record cr ON s.id = cr.student_id AND cr.status = 1 AND cr.deleted = 0 " +
            "LEFT JOIN dormitory d ON cr.dormitory_id = d.id AND d.deleted = 0 " +
            "LEFT JOIN dormitory_building db ON d.building_id = db.id AND db.deleted = 0 " +
            "WHERE s.student_no = #{studentNo} AND s.deleted = 0")
    Student selectStudentWithDormitoryByStudentNo(@Param("studentNo") String studentNo);
}
