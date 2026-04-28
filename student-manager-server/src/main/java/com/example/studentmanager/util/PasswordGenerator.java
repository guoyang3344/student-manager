package com.example.studentmanager.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String rawPassword = "123456";
        String encodedPassword = encoder.encode(rawPassword);
        
        System.out.println("原始密码: " + rawPassword);
        System.out.println("BCrypt加密后密码: " + encodedPassword);
        
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("密码匹配验证: " + matches);
        
        System.out.println("\n===== 可以在 SQL 中使用的密码 =====");
        System.out.println("密码 '123456' 的 BCrypt 加密值:");
        System.out.println(encodedPassword);
    }
}
