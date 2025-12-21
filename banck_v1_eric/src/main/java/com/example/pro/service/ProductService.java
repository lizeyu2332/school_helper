package com.example.pro.service;

import com.example.pro.entity.Product;
import com.example.pro.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    public int addProduct(Product product) {
        product.setStatus(0); // 默认待售
        return productMapper.insert(product);
    }
    
    public Product getProductById(Integer id) {
        return productMapper.selectById(id);
    }
    
    public List<Product> getAllProducts() {
        return productMapper.selectAll();
    }
    
    public List<Product> getProductsByUserId(Integer userId) {
        return productMapper.selectByUserId(userId);
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productMapper.selectByCategory(category);
    }

    public List<Product> searchProducts(String category, String keyword) {
        return productMapper.search(category, keyword);
    }
    
    public int updateProduct(Product product) {
        return productMapper.update(product);
    }
    
    public int deleteProduct(Integer id) {
        return productMapper.delete(id);
    }
}

