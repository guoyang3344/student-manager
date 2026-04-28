package com.example.studentmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("checkin_record")
public class CheckinRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long dormitoryId;

    private Long buildingId;

    private LocalDate checkinDate;

    private LocalDate checkoutDate;

    private Integer status;

    private Integer bedNumber;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String studentNo;

    @TableField(exist = false)
    private String studentName;

    @TableField(exist = false)
    private String roomNumber;

    @TableField(exist = false)
    private String buildingName;

    @TableField(exist = false)
    private String department;

    @TableField(exist = false)
    private String major;

    @TableField(exist = false)
    private String className;
}
