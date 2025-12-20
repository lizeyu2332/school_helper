package com.example.pro.controller;

import com.example.pro.entity.Product;
import com.example.pro.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = "*")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @PostMapping("/add")
    public Map<String, Object> addProduct(@RequestBody Product product) {
        Map<String, Object> result = new HashMap<>();
        try {
            productService.addProduct(product);
            result.put("success", true);
            result.put("message", "发布成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/list")
    public Map<String, Object> getAllProducts(@RequestParam(required = false) String category,
                                              @RequestParam(required = false) String keyword) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Product> products;
            if ((keyword != null && !keyword.isEmpty()) || (category != null && !category.isEmpty())) {
                products = productService.searchProducts(category, keyword);
            } else {
                products = productService.getAllProducts();
            }
            result.put("success", true);
            result.put("data", products);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getProduct(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Product product = productService.getProductById(id);
            result.put("success", true);
            result.put("data", product);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/user/{userId}")
    public Map<String, Object> getProductsByUser(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Product> products = productService.getProductsByUserId(userId);
            result.put("success", true);
            result.put("data", products);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PutMapping("/update")
    public Map<String, Object> updateProduct(@RequestBody Product product) {
        Map<String, Object> result = new HashMap<>();
        try {
            productService.updateProduct(product);
            result.put("success", true);
            result.put("message", "更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteProduct(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            productService.deleteProduct(id);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}

