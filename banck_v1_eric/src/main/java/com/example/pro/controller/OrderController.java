package com.example.pro.controller;

import com.example.pro.entity.Order;
import com.example.pro.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 创建订单（购买）
     */
    @PostMapping("/create")
    public Map<String, Object> createOrder(@RequestBody Order order) {
        Map<String, Object> result = new HashMap<>();
        try {
            orderService.createOrder(order);
            result.put("success", true);
            result.put("message", "订单创建成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 确认付款
     */
    @PostMapping("/{id}/pay")
    public Map<String, Object> confirmPay(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            orderService.confirmPay(id);
            result.put("success", true);
            result.put("message", "付款成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 卖家发货
     */
    @PostMapping("/{id}/ship")
    public Map<String, Object> shipOrder(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            orderService.shipOrder(id);
            result.put("success", true);
            result.put("message", "发货成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 确认收货
     */
    @PostMapping("/{id}/receive")
    public Map<String, Object> confirmReceive(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            orderService.confirmReceive(id);
            result.put("success", true);
            result.put("message", "确认收货成功，现在可以评价了");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    public Map<String, Object> cancelOrder(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            orderService.cancelOrder(id);
            result.put("success", true);
            result.put("message", "订单已取消");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Map<String, Object> getOrder(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Order order = orderService.getOrderById(id);
            result.put("success", true);
            result.put("data", order);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取买家的订单列表
     */
    @GetMapping("/buyer/{buyerId}")
    public Map<String, Object> getBuyerOrders(@PathVariable Integer buyerId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Order> orders = orderService.getOrdersByBuyerId(buyerId);
            result.put("success", true);
            result.put("data", orders);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取卖家的订单列表
     */
    @GetMapping("/seller/{sellerId}")
    public Map<String, Object> getSellerOrders(@PathVariable Integer sellerId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Order> orders = orderService.getOrdersBySellerId(sellerId);
            result.put("success", true);
            result.put("data", orders);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 检查是否可以评价
     */
    @GetMapping("/can-review")
    public Map<String, Object> canReview(@RequestParam Integer buyerId, @RequestParam Integer productId) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean canReview = orderService.canReview(buyerId, productId);
            result.put("success", true);
            result.put("data", canReview);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}

