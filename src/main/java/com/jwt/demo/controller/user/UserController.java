package com.jwt.demo.controller.user;

import com.jwt.demo.common.auth.TokenUser;
import com.jwt.demo.data.dto.UserDTO;
import com.jwt.demo.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public UserDTO getUserInfo(@AuthenticationPrincipal TokenUser tokenUser) {
        return userService.findUserInfo(tokenUser.getUserIdx());
    }
}
