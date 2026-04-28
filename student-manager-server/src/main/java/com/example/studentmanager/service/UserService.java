package com.example.studentmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    PageResult<User> pageUser(Integer current, Integer size, String username, String realName, Integer status);

    User getByUsername(String username);

    User getUserWithRoleById(Long id);

    boolean saveUser(User user);

    boolean updateUser(User user);

    boolean deleteUser(Long id);

    boolean updatePassword(Long id, String oldPassword, String newPassword);

    boolean resetPassword(Long id);

    String login(String username, String password);

    User getCurrentUserInfo(Long userId);
}
