package com.example.pro.service;

import com.example.pro.entity.Help;
import com.example.pro.mapper.HelpMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HelpService {
    
    @Autowired
    private HelpMapper helpMapper;
    
    public int addHelp(Help help) {
        help.setStatus(0); // 默认进行中
        return helpMapper.insert(help);
    }
    
    public Help getHelpById(Integer id) {
        return helpMapper.selectById(id);
    }
    
    public List<Help> getAllHelps() {
        return helpMapper.selectAll();
    }
    
    public List<Help> getHelpsByUserId(Integer userId) {
        return helpMapper.selectByUserId(userId);
    }
    
    public List<Help> getHelpsByCategory(String category) {
        return helpMapper.selectByCategory(category);
    }
    
    public int updateHelp(Help help) {
        return helpMapper.update(help);
    }
    
    public int deleteHelp(Integer id) {
        return helpMapper.delete(id);
    }
}

