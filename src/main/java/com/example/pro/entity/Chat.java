package com.example.pro.entity;

import lombok.Data;
import java.sql.Timestamp;

/**
 * 聊天消息实体类
 */
@Data
public class Chat {
    private Integer id;
    private Integer fromUserId;       // 发送者ID
    private Integer toUserId;          // 接收者ID
    private Integer productId;         // 关联的商品ID（可选）
    private Integer helpId;           // 关联的求助ID（可选）
    private String content;           // 消息内容
    private Integer isRead;           // 是否已读：0-未读，1-已读
    private Timestamp createTime;      // 创建时间（TIMESTAMP）
}

