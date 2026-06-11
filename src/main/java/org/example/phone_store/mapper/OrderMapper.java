package org.example.phone_store.mapper;

import org.example.phone_store.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

    void insert(Order order);

}
