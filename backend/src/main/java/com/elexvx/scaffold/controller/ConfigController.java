package com.elexvx.scaffold.controller;

import com.elexvx.scaffold.model.ConfigItem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/configs")
public class ConfigController {
    @GetMapping
    public List<ConfigItem> list() {
        List<ConfigItem> list = new ArrayList<>();
        ConfigItem c = new ConfigItem();
        c.setId(1L);
        c.setKey("site.title");
        c.setValue("ACC 管理系统");
        list.add(c);
        return list;
    }
}

