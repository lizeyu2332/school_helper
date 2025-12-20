package com.example.pro.service;

import com.example.pro.entity.DeliveryPoint;
import com.example.pro.mapper.DeliveryPointMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class DeliveryPointService {
    
    private static final String AMAP_KEY = "f4bd907075e26632db123ab93c643986";
    private static final String AMAP_GEOCODE_URL = "https://restapi.amap.com/v3/geocode/geo";
    
    @Autowired
    private DeliveryPointMapper deliveryPointMapper;
    
    public int addDeliveryPoint(DeliveryPoint deliveryPoint) {
        return deliveryPointMapper.insert(deliveryPoint);
    }
    
    public DeliveryPoint getDeliveryPointById(Integer id) {
        return deliveryPointMapper.selectById(id);
    }
    
    public List<DeliveryPoint> getAllDeliveryPoints() {
        return deliveryPointMapper.selectAll();
    }
    
    public int updateDeliveryPoint(DeliveryPoint deliveryPoint) {
        return deliveryPointMapper.update(deliveryPoint);
    }
    
    public int deleteDeliveryPoint(Integer id) {
        return deliveryPointMapper.delete(id);
    }
    
    /**
     * 使用高德地图地理编码服务获取地址的坐标
     */
    public void updateCoordinatesByGeocode(DeliveryPoint point) {
        try {
            String address = "福州大学旗山校区" + point.getName();
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
            String urlStr = AMAP_GEOCODE_URL + "?key=" + AMAP_KEY + "&address=" + encodedAddress + "&city=福州市";
            
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                // 解析 JSON 响应（简单解析，实际应该使用 JSON 库）
                String responseStr = response.toString();
                // 查找 "location" 字段
                int locationIndex = responseStr.indexOf("\"location\":\"");
                if (locationIndex > 0) {
                    int start = locationIndex + 12;
                    int end = responseStr.indexOf("\"", start);
                    if (end > start) {
                        String location = responseStr.substring(start, end);
                        String[] coords = location.split(",");
                        if (coords.length == 2) {
                            point.setLongitude(Double.parseDouble(coords[0]));
                            point.setLatitude(Double.parseDouble(coords[1]));
                            updateDeliveryPoint(point);
                        }
                    }
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            // 如果地理编码失败，使用默认坐标
            if (point.getLongitude() == null || point.getLatitude() == null) {
                point.setLongitude(119.200000);
                point.setLatitude(26.050000);
                updateDeliveryPoint(point);
            }
        }
    }
    
    /**
     * 批量更新所有送达地点的坐标
     */
    public void updateAllCoordinates() {
        List<DeliveryPoint> points = getAllDeliveryPoints();
        for (DeliveryPoint point : points) {
            // 如果坐标为空或为0，则更新
            if (point.getLongitude() == null || point.getLatitude() == null ||
                point.getLongitude() == 0 || point.getLatitude() == 0) {
                updateCoordinatesByGeocode(point);
                // 避免请求过快，稍作延迟
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

