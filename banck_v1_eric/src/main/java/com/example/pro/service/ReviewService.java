package com.example.pro.service;

import com.example.pro.entity.Review;
import com.example.pro.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;
    
    @Autowired
    private OrderService orderService;

    /**
     * 新增或更新评价（一个用户对同一商品只保留一条最新评价）
     * 只有确认收货后才能评价
     */
    public void saveOrUpdateReview(Review review) {
        // 检查是否已购买并确认收货
        if (!orderService.canReview(review.getUserId(), review.getProductId())) {
            throw new RuntimeException("只有购买并确认收货后才能评价");
        }
        
        Review exist = reviewMapper.selectByUserAndProduct(review.getUserId(), review.getProductId());
        if (exist == null) {
            reviewMapper.insert(review);
        } else {
            review.setId(exist.getId());
            reviewMapper.update(review);
        }
    }

    /**
     * 获取某商品下的评价列表 + 统计信息
     */
    public Map<String, Object> getProductReviewsWithStats(Integer productId) {
        List<Review> list = reviewMapper.selectByProductId(productId);
        double avgScore = 0.0;
        if (!list.isEmpty()) {
            int sum = 0;
            for (Review r : list) {
                if (r.getScore() != null) {
                    sum += r.getScore();
                }
            }
            avgScore = sum * 1.0 / list.size();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("avgScore", avgScore);
        result.put("count", list.size());
        return result;
    }

    public List<Review> getUserReviews(Integer userId) {
        return reviewMapper.selectByUserId(userId);
    }

    public void deleteReview(Integer id) {
        reviewMapper.delete(id);
    }
}


