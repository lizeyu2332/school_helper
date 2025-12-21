package com.example.pro.controller;

import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/geocode")
@CrossOrigin(origins = "*")
public class GeocodeController {
    
    private static final String AMAP_KEY = "f4bd907075e26632db123ab93c643986";
    private static final String AMAP_REGEOCODE_URL = "https://restapi.amap.com/v3/geocode/regeo";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 逆地理编码：根据坐标获取地址
     * @param longitude 经度
     * @param latitude 纬度
     * @return 地址信息
     */
    @GetMapping("/regeocode")
    public Map<String, Object> regeocode(@RequestParam Double longitude, @RequestParam Double latitude) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 构建请求URL
            String urlStr = String.format("%s?key=%s&location=%s,%s&radius=1000&extensions=all",
                    AMAP_REGEOCODE_URL, AMAP_KEY, longitude, latitude);
            
            System.out.println("请求高德地图API: " + urlStr);
            
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            System.out.println("HTTP响应码: " + responseCode);
            
            String responseBody = "";
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                responseBody = response.toString();
                System.out.println("高德地图API响应: " + responseBody);
            } else {
                // 读取错误响应
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                responseBody = response.toString();
                System.out.println("高德地图API错误响应: " + responseBody);
            }
            
            if (responseCode == HttpURLConnection.HTTP_OK && !responseBody.isEmpty()) {
                // 解析JSON响应
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                String status = jsonNode.has("status") ? jsonNode.get("status").asText() : "0";
                
                System.out.println("解析状态: " + status);
                
                if ("1".equals(status)) {
                    JsonNode regeocode = jsonNode.get("regeocode");
                    if (regeocode != null) {
                        JsonNode addressComponent = regeocode.get("addressComponent");
                        String formattedAddress = regeocode.has("formattedAddress") ? 
                                regeocode.get("formattedAddress").asText("") : "";
                        
                        // 提取友好地址（优先显示建筑物、POI、社区等）
                        String displayAddress = "";
                        
                        // 1. 建筑物名称
                        if (regeocode.has("building")) {
                            JsonNode building = regeocode.get("building");
                            if (building != null && building.has("name")) {
                                displayAddress = building.get("name").asText();
                            }
                        }
                        // 2. POI名称
                        if ((displayAddress == null || displayAddress.isEmpty()) && 
                            regeocode.has("pois") && regeocode.get("pois").isArray() && 
                            regeocode.get("pois").size() > 0) {
                            JsonNode poi = regeocode.get("pois").get(0);
                            if (poi.has("name")) {
                                displayAddress = poi.get("name").asText();
                            }
                        }
                        // 3. 社区名称
                        if ((displayAddress == null || displayAddress.isEmpty()) && 
                            regeocode.has("neighborhood")) {
                            JsonNode neighborhood = regeocode.get("neighborhood");
                            if (neighborhood != null && neighborhood.has("name")) {
                                displayAddress = neighborhood.get("name").asText();
                            }
                        }
                        // 4. 街道+门牌号
                        if ((displayAddress == null || displayAddress.isEmpty()) && 
                            addressComponent != null && addressComponent.has("street")) {
                            String street = addressComponent.get("street").asText("");
                            String streetNumber = addressComponent.has("streetNumber") ? 
                                    addressComponent.get("streetNumber").asText("") : "";
                            displayAddress = street + streetNumber;
                        }
                        // 5. 乡镇
                        if ((displayAddress == null || displayAddress.isEmpty()) && 
                            addressComponent != null && addressComponent.has("township")) {
                            displayAddress = addressComponent.get("township").asText("");
                        }
                        // 6. 使用完整地址
                        if (displayAddress == null || displayAddress.trim().isEmpty()) {
                            displayAddress = formattedAddress;
                        }
                        
                        // 构建返回结果
                        Map<String, Object> locationData = new HashMap<>();
                        locationData.put("longitude", longitude);
                        locationData.put("latitude", latitude);
                        locationData.put("address", formattedAddress); // 完整地址用于保存
                        locationData.put("displayAddress", displayAddress); // 友好显示地址
                        locationData.put("detailAddress", formattedAddress); // 详细地址
                        
                        result.put("success", true);
                        result.put("data", locationData);
                    } else {
                        result.put("success", false);
                        result.put("message", "无法解析地址信息，regeocode为空");
                    }
                } else {
                    String info = jsonNode.has("info") ? jsonNode.get("info").asText() : "未知错误";
                    result.put("success", false);
                    result.put("message", "高德地图API返回错误: " + info);
                }
            } else {
                result.put("success", false);
                result.put("message", "HTTP请求失败: " + responseCode + ", 响应: " + responseBody);
            }
            connection.disconnect();
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = e.getClass().getName();
            }
            result.put("success", false);
            result.put("message", "获取地址失败: " + errorMsg);
            System.err.println("获取地址异常:");
            e.printStackTrace();
        }
        return result;
    }
}

