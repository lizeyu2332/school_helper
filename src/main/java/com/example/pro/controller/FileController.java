package com.example.pro.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
@CrossOrigin(origins = "*")
public class FileController {
    
    @Value("${app.file.upload-path:D:/uploads/}")
    private String uploadPath;
    
    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (file.isEmpty()) {
                result.put("success", false);
                result.put("message", "文件为空");
                return result;
            }
            
            // 确保上传路径以/结尾
            String path = uploadPath.endsWith("/") || uploadPath.endsWith("\\") ? uploadPath : uploadPath + "/";
            
            // 创建上传目录
            File uploadDir = new File(path);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            File destFile = new File(path + newFilename);
            file.transferTo(destFile);
            
            // 返回文件URL（根据实际部署情况修改）
            String fileUrl = "/uploads/" + newFilename;
            
            result.put("success", true);
            result.put("url", fileUrl);
            result.put("message", "上传成功");
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "上传失败：" + e.getMessage());
        }
        return result;
    }
}

