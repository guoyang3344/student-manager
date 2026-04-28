package com.example.studentmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentmanager.entity.Role;
import com.example.studentmanager.exception.BusinessException;
import com.example.studentmanager.mapper.RoleMapper;
import com.example.studentmanager.mapper.UserRoleMapper;
import com.example.studentmanager.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<Role> listAll() {
        return list(new LambdaQueryWrapper<Role>()
                .eq(Role::getDeleted, 0)
                .orderByAsc(Role::getId));
    }

    @Override
    public Role getByRoleCode(String roleCode) {
        return getOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleCode, roleCode)
                .eq(Role::getDeleted, 0));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRole(Role role) {
        Role existRole = getByRoleCode(role.getRoleCode());
        if (existRole != null) {
            throw new BusinessException("角色编码已存在");
        }
        return save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(Role role) {
        Role existRole = getById(role.getId());
        if (existRole == null) {
            throw new BusinessException("角色不存在");
        }
        Role sameCodeRole = getByRoleCode(role.getRoleCode());
        if (sameCodeRole != null && !sameCodeRole.getId().equals(role.getId())) {
            throw new BusinessException("角色编码已存在");
        }
        return updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long id) {
        Role role = getById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if ("admin".equals(role.getRoleCode())) {
            throw new BusinessException("超级管理员角色不能删除");
        }
        userRoleMapper.deleteByRoleId(id);
        return removeById(id);
    }
}
