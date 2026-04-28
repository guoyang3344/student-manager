package com.example.studentmanager.controller;

import cn.hutool.core.util.StrUtil;
import com.example.studentmanager.common.Result;
import com.example.studentmanager.entity.User;
import com.example.studentmanager.service.UserService;
import com.example.studentmanager.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error("用户名或密码不能为空");
        }
        
        String token = userService.login(username, password);
        User user = userService.getByUsername(username);
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        
        return Result.success("登录成功", result);
    }

    @GetMapping("/info")
    public Result<User> getInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StrUtil.isNotBlank(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (StrUtil.isBlank(token) || !jwtUtil.validateToken(token)) {
            return Result.error(401, "未登录或登录已过期");
        }
        
        Long userId = jwtUtil.getUserId(token);
        User user = userService.getCurrentUserInfo(userId);
        
        return Result.success(user);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        if (StrUtil.isBlank(oldPassword) || StrUtil.isBlank(newPassword)) {
            return Result.error("原密码和新密码不能为空");
        }
        
        String token = request.getHeader("Authorization");
        if (StrUtil.isNotBlank(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (StrUtil.isBlank(token) || !jwtUtil.validateToken(token)) {
            return Result.error(401, "未登录或登录已过期");
        }
        
        Long userId = jwtUtil.getUserId(token);
        boolean success = userService.updatePassword(userId, oldPassword, newPassword);
        
        if (success) {
            return Result.success();
        } else {
            return Result.error("密码修改失败");
        }
    }
}
