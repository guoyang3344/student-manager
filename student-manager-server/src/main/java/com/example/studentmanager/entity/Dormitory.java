package com.example.studentmanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("dormitory")
public class Dormitory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long buildingId;

    private String roomNumber;

    private Integer floor;

    private Integer bedCount;

    private Integer occupiedBeds;

    private Integer roomType;

    private Integer status;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String buildingName;

    @TableField(exist = false)
    private String buildingNumber;
}
