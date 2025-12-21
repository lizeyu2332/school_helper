package com.example.pro.mapper;

import com.example.pro.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OrderMapper {
    int insert(Order order);
    Order selectById(Integer id);
    List<Order> selectByBuyerId(Integer buyerId);
    List<Order> selectBySellerId(Integer sellerId);
    List<Order> selectByProductId(Integer productId);
    Order selectByBuyerAndProduct(@Param("buyerId") Integer buyerId, @Param("productId") Integer productId);
    int update(Order order);
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
}

