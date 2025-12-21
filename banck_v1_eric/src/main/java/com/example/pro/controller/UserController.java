package com.example.pro.controller;

import com.example.pro.entity.User;
import com.example.pro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            userService.register(user);
            result.put("success", true);
            result.put("message", "注册成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = params.get("username");
            String password = params.get("password");
            User user = userService.login(username, password);
            // 不返回密码
            user.setPassword(null);
            result.put("success", true);
            result.put("data", user);
            result.put("message", "登录成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getUser(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userService.getUserById(id);
            user.setPassword(null);
            result.put("success", true);
            result.put("data", user);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PutMapping("/update")
    public Map<String, Object> updateUser(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            userService.updateUser(user);
            result.put("success", true);
            result.put("message", "更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}

