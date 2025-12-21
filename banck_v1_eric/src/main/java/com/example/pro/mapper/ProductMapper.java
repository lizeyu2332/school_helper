package com.example.pro.mapper;

import com.example.pro.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ProductMapper {
    int insert(Product product);
    Product selectById(Integer id);
    List<Product> selectAll();
    List<Product> selectByUserId(Integer userId);
    List<Product> selectByCategory(String category);
    List<Product> search(@org.apache.ibatis.annotations.Param("category") String category,
                         @org.apache.ibatis.annotations.Param("keyword") String keyword);
    int update(Product product);
    int delete(Integer id);
}

