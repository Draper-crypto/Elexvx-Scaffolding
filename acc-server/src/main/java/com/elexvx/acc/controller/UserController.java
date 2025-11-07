package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.elexvx.acc.model.User;
import com.elexvx.acc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @SaCheckLogin
    public List<User> list() {
        return userService.listAll();
    }
}

