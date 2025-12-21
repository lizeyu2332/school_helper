package com.example.pro.mapper;

import com.example.pro.entity.DeliveryPoint;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DeliveryPointMapper {
    int insert(DeliveryPoint deliveryPoint);
    DeliveryPoint selectById(Integer id);
    List<DeliveryPoint> selectAll();
    int update(DeliveryPoint deliveryPoint);
    int delete(Integer id);
}

