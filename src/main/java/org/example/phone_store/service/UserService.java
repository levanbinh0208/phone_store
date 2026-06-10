package org.example.phone_store.service;

import org.example.phone_store.entity.User;
import org.example.phone_store.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    public User login(String username, String rawPassword) {
        if (username == null || rawPassword == null) return null;

        User user = userMapper.findByUsername(username.trim());
        if (user == null) return null;

        if (!rawPassword.equals(user.getPasswordHash())) return null;

        return user;
    }
}
