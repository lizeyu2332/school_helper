package com.example.pro.controller;

import com.example.pro.entity.Review;
import com.example.pro.service.OrderService;
import com.example.pro.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private OrderService orderService;

    /**
     * 新增或更新评价（非匿名，前端传入当前登录用户的 userId）
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody Review review) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (review.getUserId() == null || review.getProductId() == null || review.getScore() == null) {
                result.put("success", false);
                result.put("message", "参数不完整");
                return result;
            }
            reviewService.saveOrUpdateReview(review);
            result.put("success", true);
            result.put("message", "评价成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取某商品下的所有评价 + 统计信息
     */
    @GetMapping("/product/{productId}")
    public Map<String, Object> listByProduct(@PathVariable Integer productId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> data = reviewService.getProductReviewsWithStats(productId);
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 获取某用户发表过的所有评价
     */
    @GetMapping("/user/{userId}")
    public Map<String, Object> listByUser(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Review> list = reviewService.getUserReviews(userId);
            result.put("success", true);
            result.put("data", list);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 删除评价（简单实现，未做身份校验，前端只展示“删除自己的评价”） 
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            reviewService.deleteReview(id);
            result.put("success", true);
            result.put("message", "删除成功");
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
    public Map<String, Object> canReview(@RequestParam Integer userId, @RequestParam Integer productId) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean canReview = orderService.canReview(userId, productId);
            result.put("success", true);
            result.put("data", canReview);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}


