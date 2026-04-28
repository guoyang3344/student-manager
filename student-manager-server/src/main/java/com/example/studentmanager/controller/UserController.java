package com.example.studentmanager.controller;

import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.common.Result;
import com.example.studentmanager.entity.User;
import com.example.studentmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/page")
    public Result<PageResult<User>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer status) {
        PageResult<User> result = userService.pageUser(current, size, username, realName, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getUserWithRoleById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping
    public Result<Void> save(@RequestBody User user) {
        boolean success = userService.saveUser(user);
        if (success) {
            return Result.success();
        } else {
            return Result.error("保存失败");
        }
    }

    @PutMapping
    public Result<Void> update(@RequestBody User user) {
        boolean success = userService.updateUser(user);
        if (success) {
            return Result.success();
        } else {
            return Result.error("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = userService.deleteUser(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }

    @PutMapping("/reset-password/{id}")
    public Result<Void> resetPassword(@PathVariable Long id) {
        boolean success = userService.resetPassword(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error("重置密码失败");
        }
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        if (status == null) {
            return Result.error("状态不能为空");
        }
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setStatus(status);
        boolean success = userService.updateById(user);
        if (success) {
            return Result.success();
        } else {
            return Result.error("更新状态失败");
        }
    }
}
