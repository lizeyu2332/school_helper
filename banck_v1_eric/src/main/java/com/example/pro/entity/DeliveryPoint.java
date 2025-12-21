package com.example.pro.entity;

import lombok.Data;
import java.sql.Timestamp;

/**
 * 送达点实体类
 */
@Data
public class DeliveryPoint {
    private Integer id;
    private String name;               // 地点名称
    private String address;           // 详细地址
    private Double longitude;         // 经度
    private Double latitude;          // 纬度
    private String description;       // 描述
    private Timestamp createTime;     // 创建时间（TIMESTAMP）
    private java.util.Date updateTime; // 更新时间（DATETIME）
}

