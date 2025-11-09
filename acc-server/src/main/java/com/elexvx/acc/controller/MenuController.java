package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.elexvx.acc.common.ApiResponse;
import com.elexvx.acc.dto.MenuDtos.MenuTree;
import com.elexvx.acc.service.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@Validated
public class MenuController {
  private final MenuService menuService;

  public MenuController(MenuService menuService) {
    this.menuService = menuService;
  }

  @GetMapping("/my")
  @SaCheckLogin
  public ResponseEntity<ApiResponse<List<MenuTree>>> myMenus() {
    Long userId = StpUtil.getLoginIdAsLong();
    return ResponseEntity.ok(ApiResponse.success(menuService.treeForUser(userId)));
  }
}
