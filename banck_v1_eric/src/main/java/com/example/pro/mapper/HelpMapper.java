package com.example.pro.mapper;

import com.example.pro.entity.Help;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface HelpMapper {
    int insert(Help help);
    Help selectById(Integer id);
    List<Help> selectAll();
    List<Help> selectByUserId(Integer userId);
    List<Help> selectByCategory(String category);
    int update(Help help);
    int delete(Integer id);
}

