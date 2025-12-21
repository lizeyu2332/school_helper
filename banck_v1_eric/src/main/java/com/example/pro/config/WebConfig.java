package com.example.pro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${app.file.upload-path:D:/uploads/}")
    private String uploadPath;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 不在这里配置静态资源，使用 ImageController 来处理
        // 这样可以确保 Controller 的优先级高于静态资源处理器
    }
}

