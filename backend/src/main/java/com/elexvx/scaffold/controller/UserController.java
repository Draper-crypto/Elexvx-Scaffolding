package com.elexvx.scaffold.controller;

import com.elexvx.scaffold.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @GetMapping
    public List<User> list() {
        List<User> users = new ArrayList<>();
        User u = new User();
        u.setId(1L);
        u.setUsername("admin");
        u.setNickname("管理员");
        u.setEmail("admin@example.com");
        users.add(u);
        return users;
    }

    @PostMapping
    public User create(@RequestBody User req) {
        req.setId(2L);
        return req;
    }
}

