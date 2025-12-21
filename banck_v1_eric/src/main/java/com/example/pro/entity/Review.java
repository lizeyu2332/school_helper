package com.example.pro.entity;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 商品评价实体
 */
@Data
public class Review {
    private Integer id;
    private Integer productId;
    private Integer userId;
    private Integer score;       // 1-5 星
    private String content;      // 评价内容
    private Timestamp createTime;

    // 关联展示字段（非表字段）
    private String userName;     // 用户真实姓名
    private String productTitle; // 商品标题
}


