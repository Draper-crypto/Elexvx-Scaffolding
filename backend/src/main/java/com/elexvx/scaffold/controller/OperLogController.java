package com.elexvx.scaffold.controller;

import com.elexvx.scaffold.model.OperLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/oplogs")
public class OperLogController {
    @GetMapping
    public List<OperLog> list() {
        List<OperLog> list = new ArrayList<>();
        OperLog l = new OperLog();
        l.setId(1L);
        l.setAction("登录");
        l.setOperator("admin");
        l.setTime(LocalDateTime.now());
        list.add(l);
        return list;
    }
}

