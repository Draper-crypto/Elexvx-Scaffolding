package com.elexvx.acc.dto;

import java.util.ArrayList;
import java.util.List;

public class MenuDtos {
  public static class MenuTree {
    public Long id;
    public Long parentId;
    public Integer menuType; // 1目录/2菜单/3按钮
    public String menuName;
    public String routePath;
    public String componentPath;
    public String permissionHint;
    public String icon;
    public Boolean useIconPicker;
    public Integer orderNum;
    public String externalLink;
    public String badgeText;
    public String activePath;
    public Boolean enabled;
    public Boolean cachePage;
    public Boolean hiddenMenu;
    public Boolean embedded;
    public Boolean showBadge;
    public Boolean affix;
    public Boolean hideTab;
    public Boolean fullScreen;
    public List<MenuTree> children = new ArrayList<>();
  }

  public static class MenuDetail extends MenuTree {}

  public static class MenuCreateRequest {
    public Long parentId;
    public Integer menuType;
    public String menuName;
    public String routePath;
    public String componentPath;
    public String permissionHint;
    public String icon;
    public Boolean useIconPicker;
    public Integer orderNum;
    public String externalLink;
    public String badgeText;
    public String activePath;
    public Boolean enabled;
    public Boolean cachePage;
    public Boolean hiddenMenu;
    public Boolean embedded;
    public Boolean showBadge;
    public Boolean affix;
    public Boolean hideTab;
    public Boolean fullScreen;
  }

  public static class MenuUpdateRequest extends MenuCreateRequest {}
  public static class BindMenuPermissionsRequest { public List<Long> permissionIds; }
}

