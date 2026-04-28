package com.example.studentmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("repair")
public class Repair implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long userId;

    private Long dormitoryId;

    private Long buildingId;

    private Integer repairType;

    private String title;

    private String description;

    private String imageUrls;

    private Integer status;

    private Long handlerId;

    private String handleRemark;

    private LocalDateTime handleTime;

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
    private String buildingName;

    @TableField(exist = false)
    private String roomNumber;

    @TableField(exist = false)
    private String handlerName;
}
