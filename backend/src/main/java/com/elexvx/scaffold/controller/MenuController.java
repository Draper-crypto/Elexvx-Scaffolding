package com.elexvx.scaffold.controller;

import com.elexvx.scaffold.model.Menu;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/menus")
public class MenuController {
    @GetMapping
    public List<Menu> list() {
        List<Menu> list = new ArrayList<>();
        Menu m = new Menu();
        m.setId(1L);
        m.setName("用户管理");
        m.setPath("/users");
        list.add(m);
        return list;
    }
}

