package com.example.pro.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 商品实体类
 */
@Data
public class Product {
    private Integer id;
    private Integer userId;           // 发布者ID
    private String title;             // 商品标题
    private String category;          // 分类：二手书，生活用品，历年卷
    private String description;       // 内容描述
    private BigDecimal price;         // 价格
    private String tradeType;         // 交易方式：自提、送达
    private Integer deliveryPointId;   // 送达点ID（如果选择送达）
    private Double longitude;         // 商品所在地经度
    private Double latitude;          // 商品所在地纬度
    private String locationAddress;    // 商品所在地地址（可选）
    private String images;            // 商品图片（JSON格式存储多个图片路径）
    private Integer status;            // 状态：0-待售，1-已售，2-已下架
    private Timestamp createTime;     // 创建时间（TIMESTAMP）
    private java.util.Date updateTime; // 更新时间（DATETIME）
}

