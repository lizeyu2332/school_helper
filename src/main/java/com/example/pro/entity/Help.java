package com.example.pro.entity;

import lombok.Data;
import java.sql.Timestamp;

/**
 * 求助实体类
 */
@Data
public class Help {
    private Integer id;
    private Integer userId;           // 发布者ID
    private String title;             // 求助标题
    private String category;          // 分类：拼车服务，快递代取，求购服务
    private String description;       // 内容描述
    private Double longitude;         // 求助所在地经度
    private Double latitude;          // 求助所在地纬度
    private String locationAddress;    // 求助所在地地址（可选）
    private String images;            // 相关图片（JSON格式存储多个图片路径）
    private Integer status;            // 状态：0-进行中，1-已完成，2-已取消
    private Timestamp createTime;     // 创建时间（TIMESTAMP）
    private java.util.Date updateTime; // 更新时间（DATETIME）
}

