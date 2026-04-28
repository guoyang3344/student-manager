package com.example.studentmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentmanager.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT u.*, r.role_name, r.id as role_id FROM sys_user u " +
            "LEFT JOIN sys_user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN sys_role r ON ur.role_id = r.id " +
            "WHERE u.deleted = 0")
    List<User> selectUserWithRole();

    @Select("SELECT u.*, r.role_name, r.id as role_id FROM sys_user u " +
            "LEFT JOIN sys_user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN sys_role r ON ur.role_id = r.id " +
            "WHERE u.id = #{id} AND u.deleted = 0")
    User selectUserWithRoleById(@Param("id") Long id);

    @Select("SELECT u.*, r.role_name, r.id as role_id FROM sys_user u " +
            "LEFT JOIN sys_user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN sys_role r ON ur.role_id = r.id " +
            "WHERE u.username = #{username} AND u.deleted = 0")
    User selectUserWithRoleByUsername(@Param("username") String username);
}
