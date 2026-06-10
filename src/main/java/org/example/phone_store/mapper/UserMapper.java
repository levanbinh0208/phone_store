package org.example.phone_store.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.phone_store.entity.User;

@Mapper
public interface UserMapper {
     User findByUsername(@Param("username") String username);
}
