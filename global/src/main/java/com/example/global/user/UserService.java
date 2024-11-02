package com.example.global.user;




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

        // 사용자 존재 여부 확인
        if (userRepository.existsByUsername(userDto.getUsername())) {
            return;
        }


        Role role = Role.USER;
        if (userDto.getRole() != null && userDto.getRole().equalsIgnoreCase("admin")) {
            role = Role.ADMIN;
        }

        User data = new User(userDto.getUsername(), bCryptPasswordEncoder.encode(userDto.getPassword()), role);
        userRepository.save(data);
    }

}
