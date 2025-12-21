package com.example.pro.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 订单实体类
 */
@Data
public class Order {
    private Integer id;
    private Integer productId;          // 商品ID
    private Integer buyerId;            // 买家ID
    private Integer sellerId;            // 卖家ID（商品发布者）
    private BigDecimal price;            // 订单金额
    private Integer status;              // 状态：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消
    private String remark;               // 买家备注
    private Timestamp createTime;        // 创建时间
    private Timestamp payTime;           // 付款时间
    private Timestamp shipTime;          // 发货时间
    private Timestamp receiveTime;       // 确认收货时间
    private String productTitle;         // 商品名称（JOIN查询时填充）
}

