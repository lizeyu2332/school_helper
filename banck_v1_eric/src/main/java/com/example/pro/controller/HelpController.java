package com.example.pro.controller;

import com.example.pro.entity.Help;
import com.example.pro.service.HelpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/help")
@CrossOrigin(origins = "*")
public class HelpController {
    
    @Autowired
    private HelpService helpService;
    
    @PostMapping("/add")
    public Map<String, Object> addHelp(@RequestBody Help help) {
        Map<String, Object> result = new HashMap<>();
        try {
            helpService.addHelp(help);
            result.put("success", true);
            result.put("message", "发布成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/list")
    public Map<String, Object> getAllHelps(@RequestParam(required = false) String category) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Help> helps;
            if (category != null && !category.isEmpty()) {
                helps = helpService.getHelpsByCategory(category);
            } else {
                helps = helpService.getAllHelps();
            }
            result.put("success", true);
            result.put("data", helps);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getHelp(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Help help = helpService.getHelpById(id);
            result.put("success", true);
            result.put("data", help);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/user/{userId}")
    public Map<String, Object> getHelpsByUser(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Help> helps = helpService.getHelpsByUserId(userId);
            result.put("success", true);
            result.put("data", helps);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PutMapping("/update")
    public Map<String, Object> updateHelp(@RequestBody Help help) {
        Map<String, Object> result = new HashMap<>();
        try {
            helpService.updateHelp(help);
            result.put("success", true);
            result.put("message", "更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteHelp(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            helpService.deleteHelp(id);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}

