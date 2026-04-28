package com.example.studentmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentmanager.entity.CheckinRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CheckinRecordMapper extends BaseMapper<CheckinRecord> {

    @Select("SELECT cr.*, s.student_no, s.name as student_name, s.department, s.major, s.class_name, " +
            "d.room_number, db.building_name " +
            "FROM checkin_record cr " +
            "LEFT JOIN student s ON cr.student_id = s.id AND s.deleted = 0 " +
            "LEFT JOIN dormitory d ON cr.dormitory_id = d.id AND d.deleted = 0 " +
            "LEFT JOIN dormitory_building db ON cr.building_id = db.id AND db.deleted = 0 " +
            "WHERE cr.deleted = 0")
    List<CheckinRecord> selectCheckinRecordWithDetail();

    @Select("SELECT cr.*, s.student_no, s.name as student_name, s.department, s.major, s.class_name, " +
            "d.room_number, db.building_name " +
            "FROM checkin_record cr " +
            "LEFT JOIN student s ON cr.student_id = s.id AND s.deleted = 0 " +
            "LEFT JOIN dormitory d ON cr.dormitory_id = d.id AND d.deleted = 0 " +
            "LEFT JOIN dormitory_building db ON cr.building_id = db.id AND db.deleted = 0 " +
            "WHERE cr.id = #{id} AND cr.deleted = 0")
    CheckinRecord selectCheckinRecordWithDetailById(@Param("id") Long id);

    @Select("SELECT cr.*, s.student_no, s.name as student_name, s.department, s.major, s.class_name, " +
            "d.room_number, db.building_name " +
            "FROM checkin_record cr " +
            "LEFT JOIN student s ON cr.student_id = s.id AND s.deleted = 0 " +
            "LEFT JOIN dormitory d ON cr.dormitory_id = d.id AND d.deleted = 0 " +
            "LEFT JOIN dormitory_building db ON cr.building_id = db.id AND db.deleted = 0 " +
            "WHERE cr.student_id = #{studentId} AND cr.deleted = 0")
    List<CheckinRecord> selectCheckinRecordWithDetailByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT cr.*, s.student_no, s.name as student_name, s.department, s.major, s.class_name, " +
            "d.room_number, db.building_name " +
            "FROM checkin_record cr " +
            "LEFT JOIN student s ON cr.student_id = s.id AND s.deleted = 0 " +
            "LEFT JOIN dormitory d ON cr.dormitory_id = d.id AND d.deleted = 0 " +
            "LEFT JOIN dormitory_building db ON cr.building_id = db.id AND db.deleted = 0 " +
            "WHERE cr.dormitory_id = #{dormitoryId} AND cr.deleted = 0")
    List<CheckinRecord> selectCheckinRecordWithDetailByDormitoryId(@Param("dormitoryId") Long dormitoryId);
}
