package com.example.studentmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentmanager.entity.Repair;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RepairMapper extends BaseMapper<Repair> {

    @Select("SELECT r.*, s.student_no, s.name as student_name, db.building_name, d.room_number, u.real_name as handler_name " +
            "FROM repair r " +
            "LEFT JOIN student s ON r.student_id = s.id AND s.deleted = 0 " +
            "LEFT JOIN dormitory d ON r.dormitory_id = d.id AND d.deleted = 0 " +
            "LEFT JOIN dormitory_building db ON r.building_id = db.id AND db.deleted = 0 " +
            "LEFT JOIN sys_user u ON r.handler_id = u.id AND u.deleted = 0 " +
            "WHERE r.deleted = 0")
    List<Repair> selectRepairWithDetail();

    @Select("SELECT r.*, s.student_no, s.name as student_name, db.building_name, d.room_number, u.real_name as handler_name " +
            "FROM repair r " +
            "LEFT JOIN student s ON r.student_id = s.id AND s.deleted = 0 " +
            "LEFT JOIN dormitory d ON r.dormitory_id = d.id AND d.deleted = 0 " +
            "LEFT JOIN dormitory_building db ON r.building_id = db.id AND db.deleted = 0 " +
            "LEFT JOIN sys_user u ON r.handler_id = u.id AND u.deleted = 0 " +
            "WHERE r.id = #{id} AND r.deleted = 0")
    Repair selectRepairWithDetailById(@Param("id") Long id);

    @Select("SELECT r.*, s.student_no, s.name as student_name, db.building_name, d.room_number, u.real_name as handler_name " +
            "FROM repair r " +
            "LEFT JOIN student s ON r.student_id = s.id AND s.deleted = 0 " +
            "LEFT JOIN dormitory d ON r.dormitory_id = d.id AND d.deleted = 0 " +
            "LEFT JOIN dormitory_building db ON r.building_id = db.id AND db.deleted = 0 " +
            "LEFT JOIN sys_user u ON r.handler_id = u.id AND u.deleted = 0 " +
            "WHERE r.student_id = #{studentId} AND r.deleted = 0")
    List<Repair> selectRepairWithDetailByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT r.*, s.student_no, s.name as student_name, db.building_name, d.room_number, u.real_name as handler_name " +
            "FROM repair r " +
            "LEFT JOIN student s ON r.student_id = s.id AND s.deleted = 0 " +
            "LEFT JOIN dormitory d ON r.dormitory_id = d.id AND d.deleted = 0 " +
            "LEFT JOIN dormitory_building db ON r.building_id = db.id AND db.deleted = 0 " +
            "LEFT JOIN sys_user u ON r.handler_id = u.id AND u.deleted = 0 " +
            "WHERE r.dormitory_id = #{dormitoryId} AND r.deleted = 0")
    List<Repair> selectRepairWithDetailByDormitoryId(@Param("dormitoryId") Long dormitoryId);
}
