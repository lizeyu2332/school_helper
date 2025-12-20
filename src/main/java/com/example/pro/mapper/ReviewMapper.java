package com.example.pro.mapper;

import com.example.pro.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {
    int insert(Review review);

    int update(Review review);

    Review selectByUserAndProduct(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<Review> selectByProductId(Integer productId);

    List<Review> selectByUserId(Integer userId);

    int delete(Integer id);
}


