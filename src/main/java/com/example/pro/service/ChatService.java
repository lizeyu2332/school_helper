package com.example.pro.service;

import com.example.pro.entity.Chat;
import com.example.pro.mapper.ChatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    
    @Autowired
    private ChatMapper chatMapper;
    
    public int sendMessage(Chat chat) {
        chat.setIsRead(0); // 默认未读
        return chatMapper.insert(chat);
    }
    
    public List<Chat> getChatHistory(Integer userId1, Integer userId2) {
        return chatMapper.selectByUserPair(userId1, userId2);
    }
    
    public List<Chat> getUnreadMessages(Integer userId) {
        return chatMapper.selectUnreadByUserId(userId);
    }
    
    public int markAsRead(Integer id) {
        return chatMapper.updateReadStatus(id, 1);
    }
    
    public int markAllAsRead(Integer fromUserId, Integer toUserId) {
        return chatMapper.markAllRead(fromUserId, toUserId);
    }
    
    public List<Chat> getAllChatsByUserId(Integer userId) {
        return chatMapper.selectAllByUserId(userId);
    }
}

