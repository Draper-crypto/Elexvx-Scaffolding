package com.elexvx.scaffold.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.elexvx.scaffold.dto.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest req) {
        if ("admin".equals(req.getUsername()) && "admin123".equals(req.getPassword())) {
            StpUtil.login(1);
            SaTokenInfo info = StpUtil.getTokenInfo();
            Map<String, Object> resp = new HashMap<>();
            resp.put("tokenValue", info.getTokenValue());
            resp.put("loginId", StpUtil.getLoginId());
            return resp;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    @GetMapping("/me")
    public Map<String, Object> me() {
        StpUtil.checkLogin();
        Map<String, Object> resp = new HashMap<>();
        resp.put("loginId", StpUtil.getLoginId());
        resp.put("tokenValue", StpUtil.getTokenValue());
        return resp;
    }

    @PostMapping("/logout")
    public void logout() {
        StpUtil.logout();
    }
}

