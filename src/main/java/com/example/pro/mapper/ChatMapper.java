package com.example.pro.mapper;

import com.example.pro.entity.Chat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChatMapper {
    int insert(Chat chat);
    Chat selectById(Integer id);
    List<Chat> selectByUserPair(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);
    List<Chat> selectUnreadByUserId(Integer userId);
    List<Chat> selectAllByUserId(Integer userId);
    int updateReadStatus(@Param("id") Integer id, @Param("isRead") Integer isRead);
    int markAllRead(@Param("fromUserId") Integer fromUserId, @Param("toUserId") Integer toUserId);
}

