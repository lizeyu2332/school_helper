package com.example.pro.entity;

import lombok.Data;
import java.sql.Timestamp;

/**
 * 用户实体类
 */
@Data
public class User {
    private Integer id;
    private String username;      // 用户名
    private String password;      // 密码
    private String realName;      // 真实姓名
    private String studentId;     // 学号
    private String phone;         // 手机号
    private String email;         // 邮箱
    private Timestamp createTime; // 创建时间（TIMESTAMP）
    private java.util.Date updateTime; // 更新时间（DATETIME）
}

