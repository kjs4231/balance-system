package com.example.global.user;

public interface CustomUserRepository {
    User findByUsername(String username);
    User findByUserId(Long userId);
}
