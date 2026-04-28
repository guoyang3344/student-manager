package com.example.studentmanager.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentmanager.common.PageResult;
import com.example.studentmanager.entity.User;
import com.example.studentmanager.entity.UserRole;
import com.example.studentmanager.exception.BusinessException;
import com.example.studentmanager.mapper.UserMapper;
import com.example.studentmanager.mapper.UserRoleMapper;
import com.example.studentmanager.service.UserService;
import com.example.studentmanager.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public PageResult<User> pageUser(Integer current, Integer size, String username, String realName, Integer status) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, 0);
        if (StrUtil.isNotBlank(username)) {
            wrapper.like(User::getUsername, username);
        }
        if (StrUtil.isNotBlank(realName)) {
            wrapper.like(User::getRealName, realName);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> resultPage = page(page, wrapper);
        
        List<User> users = baseMapper.selectUserWithRole();
        return new PageResult<>(
                resultPage.getTotal(),
                users,
                resultPage.getCurrent(),
                resultPage.getSize(),
                resultPage.getPages()
        );
    }

    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0));
    }

    @Override
    public User getUserWithRoleById(Long id) {
        return baseMapper.selectUserWithRoleById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveUser(User user) {
        User existUser = getByUsername(user.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setStatus(1);
        boolean saved = save(user);
        if (saved && user.getRoleId() != null) {
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(user.getRoleId());
            userRoleMapper.insert(userRole);
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(User user) {
        User existUser = getById(user.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        User sameUsernameUser = getByUsername(user.getUsername());
        if (sameUsernameUser != null && !sameUsernameUser.getId().equals(user.getId())) {
            throw new BusinessException("用户名已存在");
        }
        user.setPassword(null);
        boolean updated = updateById(user);
        if (updated && user.getRoleId() != null) {
            userRoleMapper.deleteByUserId(user.getId());
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(user.getRoleId());
            userRoleMapper.insert(userRole);
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException("超级管理员不能删除");
        }
        userRoleMapper.deleteByUserId(id);
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(Long id, String oldPassword, String newPassword) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        return updateById(user);
    }

    @Override
    public String login(String username, String password) {
        User user = baseMapper.selectUserWithRoleByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用，请联系管理员");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }

    @Override
    public User getCurrentUserInfo(Long userId) {
        User user = baseMapper.selectUserWithRoleById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }
}
