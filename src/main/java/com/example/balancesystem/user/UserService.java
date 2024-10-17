package com.example.balancesystem.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void registerUser(UserDto userDto) {

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("이미 있는 유저입니다.");
        }

        User user = new User(userDto.getUsername(), userDto.getPassword(), userDto.getRole());

        userRepository.save(user);
    }
}