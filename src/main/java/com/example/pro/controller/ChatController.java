package com.example.pro.controller;

import com.example.pro.entity.Chat;
import com.example.pro.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    @PostMapping("/send")
    public Map<String, Object> sendMessage(@RequestBody Chat chat) {
        Map<String, Object> result = new HashMap<>();
        try {
            chatService.sendMessage(chat);
            result.put("success", true);
            result.put("message", "发送成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/history")
    public Map<String, Object> getChatHistory(@RequestParam Integer userId1, @RequestParam Integer userId2) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Chat> chats = chatService.getChatHistory(userId1, userId2);
            result.put("success", true);
            result.put("data", chats);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/unread/{userId}")
    public Map<String, Object> getUnreadMessages(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Chat> chats = chatService.getUnreadMessages(userId);
            result.put("success", true);
            result.put("data", chats);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PutMapping("/read/{id}")
    public Map<String, Object> markAsRead(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            chatService.markAsRead(id);
            result.put("success", true);
            result.put("message", "已标记为已读");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @PutMapping("/readAll")
    public Map<String, Object> markAllAsRead(@RequestParam Integer fromUserId, @RequestParam Integer toUserId) {
        Map<String, Object> result = new HashMap<>();
        try {
            chatService.markAllAsRead(fromUserId, toUserId);
            result.put("success", true);
            result.put("message", "已全部标记为已读");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/all/{userId}")
    public Map<String, Object> getAllChats(@PathVariable Integer userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Chat> chats = chatService.getAllChatsByUserId(userId);
            result.put("success", true);
            result.put("data", chats);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}

