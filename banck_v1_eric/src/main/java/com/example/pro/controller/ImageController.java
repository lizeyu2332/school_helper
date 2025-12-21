package com.example.pro.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ImageController {
    
    @Value("${app.file.upload-path:D:/uploads/}")
    private String uploadPath;
    
    // 测试接口
    @GetMapping("/test/image")
    public Map<String, Object> testImage() {
        Map<String, Object> result = new HashMap<>();
        result.put("uploadPath", uploadPath);
        result.put("message", "ImageController is working");
        return result;
    }
    
    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            // 处理上传路径 - Windows路径处理
            String path = uploadPath.replace("\\", "/");
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            
            // 构建完整文件路径
            File file = new File(path + filename);
            
            // 调试日志
            System.out.println("=== ImageController Called ===");
            System.out.println("Filename: " + filename);
            System.out.println("Upload path: " + path);
            System.out.println("Full file path: " + file.getAbsolutePath());
            System.out.println("File exists: " + file.exists());
            
            if (!file.exists() || !file.isFile()) {
                System.out.println("File not found: " + file.getAbsolutePath());
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(file);
            String contentType = null;
            try {
                contentType = Files.probeContentType(file.toPath());
            } catch (Exception e) {
                // 忽略
            }
            
            if (contentType == null) {
                // 根据文件扩展名判断
                String fileName = file.getName().toLowerCase();
                if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (fileName.endsWith(".png")) {
                    contentType = "image/png";
                } else if (fileName.endsWith(".gif")) {
                    contentType = "image/gif";
                } else {
                    contentType = "application/octet-stream";
                }
            }
            
            System.out.println("Returning image with content type: " + contentType);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in ImageController: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
