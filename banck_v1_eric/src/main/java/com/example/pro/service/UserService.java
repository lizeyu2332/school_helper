package com.example.pro.service;

import com.example.pro.entity.User;
import com.example.pro.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    public int register(User user) {
        // 检查用户名是否已存在
        User existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 检查学号是否已存在
        User existStudent = userMapper.selectByStudentId(user.getStudentId());
        if (existStudent != null) {
            throw new RuntimeException("学号已被注册");
        }
        return userMapper.insert(user);
    }
    
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("用户名或密码错误");
        }
        return user;
    }
    
    public User getUserById(Integer id) {
        return userMapper.selectById(id);
    }
    
    public int updateUser(User user) {
        return userMapper.update(user);
    }
}

