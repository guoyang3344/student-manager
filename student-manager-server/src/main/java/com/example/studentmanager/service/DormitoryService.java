package com.example.studentmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.entity.Dormitory;

import java.util.List;

public interface DormitoryService extends IService<Dormitory> {

    PageResult<Dormitory> pageDormitory(Integer current, Integer size, String roomNumber, Long buildingId, Integer status);

    Dormitory getDormitoryWithBuildingById(Long id);

    List<Dormitory> listByBuildingId(Long buildingId);

    List<Dormitory> listAvailable(Long buildingId);

    boolean saveDormitory(Dormitory dormitory);

    boolean updateDormitory(Dormitory dormitory);

    boolean deleteDormitory(Long id);
}
