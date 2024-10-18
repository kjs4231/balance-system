package com.example.balancesystem.domain.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void signup(UserDto userDto) {

        String username = userDto.getUsername();
        String password = userDto.getPassword();

        if (userRepository.existsByUsername(username)) {
            return;
        }

        User data = new User(username, bCryptPasswordEncoder.encode(password));
        userRepository.save(data);
    }

}
