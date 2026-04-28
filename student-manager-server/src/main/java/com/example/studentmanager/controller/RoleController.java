package com.example.studentmanager.controller;

import com.example.studentmanager.common.Result;
import com.example.studentmanager.entity.Role;
import com.example.studentmanager.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public Result<List<Role>> list() {
        List<Role> roles = roleService.listAll();
        return Result.success(roles);
    }

    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }

    @PostMapping
    public Result<Void> save(@RequestBody Role role) {
        boolean success = roleService.saveRole(role);
        if (success) {
            return Result.success();
        } else {
            return Result.error("保存失败");
        }
    }

    @PutMapping
    public Result<Void> update(@RequestBody Role role) {
        boolean success = roleService.updateRole(role);
        if (success) {
            return Result.success();
        } else {
            return Result.error("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = roleService.deleteRole(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }
}
