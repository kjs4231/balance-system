package com.example.global.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {

    private String username;
    private String password;
    private String role;
}
