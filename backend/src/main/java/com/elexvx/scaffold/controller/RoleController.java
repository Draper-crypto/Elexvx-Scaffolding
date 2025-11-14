package com.elexvx.scaffold.controller;

import com.elexvx.scaffold.model.Role;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    @GetMapping
    public List<Role> list() {
        List<Role> list = new ArrayList<>();
        Role r = new Role();
        r.setId(1L);
        r.setName("管理员");
        r.setCode("ADMIN");
        list.add(r);
        return list;
    }
}

