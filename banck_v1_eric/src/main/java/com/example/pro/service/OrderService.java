package com.example.pro.service;

import com.example.pro.entity.Order;
import com.example.pro.entity.Product;
import com.example.pro.mapper.OrderMapper;
import com.example.pro.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    /**
     * 创建订单（购买）
     */
    @Transactional
    public int createOrder(Order order) {
        // 检查商品是否存在且可购买
        Product product = productMapper.selectById(order.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStatus() != 0) {
            throw new RuntimeException("商品已售出或已下架");
        }
        if (product.getUserId().equals(order.getBuyerId())) {
            throw new RuntimeException("不能购买自己发布的商品");
        }
        
        // 检查是否已有未完成的订单
        Order existingOrder = orderMapper.selectByBuyerAndProduct(order.getBuyerId(), order.getProductId());
        if (existingOrder != null && existingOrder.getStatus() != 4) {
            throw new RuntimeException("该商品已有未完成的订单");
        }
        
        order.setSellerId(product.getUserId());
        order.setPrice(product.getPrice());
        order.setStatus(0); // 待付款
        return orderMapper.insert(order);
    }
    
    /**
     * 确认付款
     */
    @Transactional
    public int confirmPay(Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不正确");
        }
        orderMapper.updateStatus(orderId, 1); // 待发货
        return 1;
    }
    
    /**
     * 卖家发货
     */
    @Transactional
    public int shipOrder(Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不正确");
        }
        orderMapper.updateStatus(orderId, 2); // 待收货
        return 1;
    }
    
    /**
     * 确认收货（完成后可以评价）
     */
    @Transactional
    public int confirmReceive(Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new RuntimeException("订单状态不正确");
        }
        orderMapper.updateStatus(orderId, 3); // 已完成
        
        // 更新商品状态为已售
        Product product = productMapper.selectById(order.getProductId());
        if (product != null && product.getStatus() == 0) {
            product.setStatus(1); // 已售
            productMapper.update(product);
        }
        
        return 1;
    }
    
    /**
     * 取消订单
     */
    @Transactional
    public int cancelOrder(Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() == 3 || order.getStatus() == 4) {
            throw new RuntimeException("订单已完成或已取消");
        }
        orderMapper.updateStatus(orderId, 4); // 已取消
        return 1;
    }
    
    public Order getOrderById(Integer id) {
        return orderMapper.selectById(id);
    }
    
    public List<Order> getOrdersByBuyerId(Integer buyerId) {
        return orderMapper.selectByBuyerId(buyerId);
    }
    
    public List<Order> getOrdersBySellerId(Integer sellerId) {
        return orderMapper.selectBySellerId(sellerId);
    }
    
    /**
     * 检查买家是否已购买并确认收货（可以评价）
     */
    public boolean canReview(Integer buyerId, Integer productId) {
        Order order = orderMapper.selectByBuyerAndProduct(buyerId, productId);
        return order != null && order.getStatus() == 3; // 已完成
    }
}

