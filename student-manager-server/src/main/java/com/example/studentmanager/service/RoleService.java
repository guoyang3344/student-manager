package com.example.studentmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentmanager.entity.Role;

import java.util.List;

public interface RoleService extends IService<Role> {

    List<Role> listAll();

    Role getByRoleCode(String roleCode);

    boolean saveRole(Role role);

    boolean updateRole(Role role);

    boolean deleteRole(Long id);
}
