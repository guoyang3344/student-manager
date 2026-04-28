package com.example.studentmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("student")
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String studentNo;

    private String name;

    private Integer gender;

    private LocalDate birthday;

    private String idCard;

    private String phone;

    private String email;

    private String department;

    private String major;

    private String className;

    private Integer grade;

    private String address;

    private String avatar;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String dormitoryRoomNumber;

    @TableField(exist = false)
    private String buildingName;

    @TableField(exist = false)
    private Integer checkinStatus;
}
