package com.example.balancesystem.domain.user;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {

        this.userService = userService;
    }

    @PostMapping("/join")
    public ResponseEntity<String> signup(@RequestBody UserDto userDto) {

        userService.signup(userDto);

        return ResponseEntity.ok("ok");
    }

}
