package com.example.pro.mapper;

import com.example.pro.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    int insert(User user);
    User selectById(Integer id);
    User selectByUsername(String username);
    User selectByStudentId(String studentId);
    int update(User user);
}

