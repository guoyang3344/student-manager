package com.example.studentmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentmanager.entity.Dormitory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DormitoryMapper extends BaseMapper<Dormitory> {

    @Select("SELECT d.*, db.building_name, db.building_number FROM dormitory d " +
            "LEFT JOIN dormitory_building db ON d.building_id = db.id " +
            "WHERE d.deleted = 0")
    List<Dormitory> selectDormitoryWithBuilding();

    @Select("SELECT d.*, db.building_name, db.building_number FROM dormitory d " +
            "LEFT JOIN dormitory_building db ON d.building_id = db.id " +
            "WHERE d.id = #{id} AND d.deleted = 0")
    Dormitory selectDormitoryWithBuildingById(@Param("id") Long id);

    @Select("SELECT d.*, db.building_name, db.building_number FROM dormitory d " +
            "LEFT JOIN dormitory_building db ON d.building_id = db.id " +
            "WHERE d.building_id = #{buildingId} AND d.deleted = 0")
    List<Dormitory> selectDormitoryWithBuildingByBuildingId(@Param("buildingId") Long buildingId);
}
