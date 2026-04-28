package com.example.studentmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.entity.DormitoryBuilding;

import java.util.List;

public interface DormitoryBuildingService extends IService<DormitoryBuilding> {

    PageResult<DormitoryBuilding> pageBuilding(Integer current, Integer size, String buildingName, Integer genderType, Integer status);

    List<DormitoryBuilding> listAll();

    boolean saveBuilding(DormitoryBuilding building);

    boolean updateBuilding(DormitoryBuilding building);

    boolean deleteBuilding(Long id);
}
