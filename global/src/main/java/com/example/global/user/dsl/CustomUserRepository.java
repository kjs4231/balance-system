package com.example.global.user.dsl;

import com.example.global.user.User;

public interface CustomUserRepository {
    User findByUsername(String username);
    User findByUserId(Long userId);
}
