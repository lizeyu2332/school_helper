package com.example.pro.controller;

import com.example.pro.entity.DeliveryPoint;
import com.example.pro.service.DeliveryPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
@CrossOrigin(origins = "*")
public class DeliveryPointController {
    
    @Autowired
    private DeliveryPointService deliveryPointService;
    
    @PostMapping("/add")
    public Map<String, Object> addDeliveryPoint(@RequestBody DeliveryPoint deliveryPoint) {
        Map<String, Object> result = new HashMap<>();
        try {
            deliveryPointService.addDeliveryPoint(deliveryPoint);
            result.put("success", true);
            result.put("message", "添加成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/list")
    public Map<String, Object> getAllDeliveryPoints() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<DeliveryPoint> points = deliveryPointService.getAllDeliveryPoints();
            result.put("success", true);
            result.put("data", points);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getDeliveryPoint(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            DeliveryPoint point = deliveryPointService.getDeliveryPointById(id);
            result.put("success", true);
            result.put("data", point);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PutMapping("/update")
    public Map<String, Object> updateDeliveryPoint(@RequestBody DeliveryPoint deliveryPoint) {
        Map<String, Object> result = new HashMap<>();
        try {
            deliveryPointService.updateDeliveryPoint(deliveryPoint);
            result.put("success", true);
            result.put("message", "更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteDeliveryPoint(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            deliveryPointService.deleteDeliveryPoint(id);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 批量更新所有送达地点的坐标（使用高德地图地理编码服务）
     */
    @PostMapping("/update-coordinates")
    public Map<String, Object> updateAllCoordinates() {
        Map<String, Object> result = new HashMap<>();
        try {
            deliveryPointService.updateAllCoordinates();
            result.put("success", true);
            result.put("message", "坐标更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 更新单个送达地点的坐标
     */
    @PostMapping("/update-coordinate/{id}")
    public Map<String, Object> updateCoordinate(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            DeliveryPoint point = deliveryPointService.getDeliveryPointById(id);
            if (point != null) {
                deliveryPointService.updateCoordinatesByGeocode(point);
                result.put("success", true);
                result.put("message", "坐标更新成功");
            } else {
                result.put("success", false);
                result.put("message", "送达地点不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}

