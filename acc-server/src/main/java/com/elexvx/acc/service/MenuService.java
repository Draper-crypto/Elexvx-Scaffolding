package com.elexvx.acc.service;

import com.elexvx.acc.dto.MenuDtos.*;
import com.elexvx.acc.entity.SysMenu;
import com.elexvx.acc.entity.SysMenuPermission;
import com.elexvx.acc.entity.SysPermission;
import com.elexvx.acc.entity.SysRole;
import com.elexvx.acc.entity.SysRoleMenu;
import com.elexvx.acc.entity.SysRolePermission;
import com.elexvx.acc.entity.SysUserRole;
import com.elexvx.acc.repo.SysMenuPermissionRepository;
import com.elexvx.acc.repo.SysMenuRepository;
import com.elexvx.acc.repo.SysPermissionRepository;
import com.elexvx.acc.repo.SysRoleMenuRepository;
import com.elexvx.acc.repo.SysRolePermissionRepository;
import com.elexvx.acc.repo.SysRoleRepository;
import com.elexvx.acc.repo.SysUserRoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MenuService {
  private final SysMenuRepository menuRepo;
  private final SysMenuPermissionRepository menuPermRepo;
  private final SysPermissionRepository permRepo;
  private final SysRolePermissionRepository rolePermRepo;
  private final SysRoleMenuRepository roleMenuRepo;
  private final SysUserRoleRepository userRoleRepo;
  private final SysRoleRepository roleRepo;

  public MenuService(SysMenuRepository menuRepo,
                     SysMenuPermissionRepository menuPermRepo,
                     SysPermissionRepository permRepo,
                     SysRolePermissionRepository rolePermRepo,
                     SysRoleMenuRepository roleMenuRepo,
                     SysUserRoleRepository userRoleRepo,
                     SysRoleRepository roleRepo) {
    this.menuRepo = menuRepo;
    this.menuPermRepo = menuPermRepo;
    this.permRepo = permRepo;
    this.rolePermRepo = rolePermRepo;
    this.roleMenuRepo = roleMenuRepo;
    this.userRoleRepo = userRoleRepo;
    this.roleRepo = roleRepo;
  }

  @PostConstruct
  public void initBuiltinMenus() {
    ensureChangeLogMenuExists();
  }

  public List<MenuTree> tree() {
    return buildTree(null);
  }

  public List<MenuTree> treeForUser(Long userId) {
    if (userId == null) {
      return new ArrayList<>();
    }
    List<SysUserRole> userRoles = userRoleRepo.findByUserId(userId);
    if (userRoles.isEmpty()) {
      return new ArrayList<>();
    }
    List<Long> roleIds = userRoles.stream()
        .map(SysUserRole::getRoleId)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
    if (roleIds.isEmpty()) {
      return new ArrayList<>();
    }
    if (hasSuperRole(roleIds)) {
      return tree();
    }
    List<SysRoleMenu> roleMenus = roleMenuRepo.findByRoleIdIn(roleIds);
    if (roleMenus == null || roleMenus.isEmpty()) {
      return new ArrayList<>();
    }
    Set<Long> menuIds = roleMenus.stream()
        .map(SysRoleMenu::getMenuId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
    if (menuIds.isEmpty()) {
      return new ArrayList<>();
    }
    Set<Long> effectiveMenuIds = includeAncestorMenus(menuIds);
    Set<Long> permIds = rolePermRepo.findByRoleIdIn(roleIds).stream()
        .map(SysRolePermission::getPermissionId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
    List<MenuTree> fullTree = tree();
    return filterMenuTree(fullTree, effectiveMenuIds, permIds);
  }

  private List<MenuTree> buildTree(Long parentId) {
    List<SysMenu> list = menuRepo.findByParentIdOrderByOrderNumAsc(parentId);
    List<MenuTree> result = new ArrayList<>();
    for (SysMenu m : list) {
      MenuTree node = toTree(m);
      node.children = buildTree(m.getId());
      result.add(node);
    }
    return result;
  }

  public MenuDetail get(Long id) {
    SysMenu m = menuRepo.findById(id).orElseThrow(() -> new RuntimeException("Menu not found"));
    return toDetail(m);
  }

  @Transactional
  public MenuDetail create(MenuCreateRequest req) {
    SysMenu m = fromCreate(req);
    menuRepo.save(m);
    return toDetail(m);
  }

  @Transactional
  public MenuDetail update(Long id, MenuUpdateRequest req) {
    SysMenu m = menuRepo.findById(id).orElseThrow(() -> new RuntimeException("Menu not found"));
    applyUpdate(m, req);
    menuRepo.save(m);
    return toDetail(m);
  }

  @Transactional
  public void delete(Long id) {
    // 递归删除子节点
    List<SysMenu> children = menuRepo.findByParentIdOrderByOrderNumAsc(id);
    for (SysMenu child : children) {
      delete(child.getId());
    }
    // 删除菜单与权限绑定
    List<SysMenuPermission> bindings = menuPermRepo.findByMenuId(id);
    for (SysMenuPermission binding : bindings) {
      removePermissionBinding(binding.getPermissionId(), binding.getMenuId());
    }
    roleMenuRepo.deleteByMenuId(id);
    menuRepo.deleteById(id);
  }

  @Transactional
  public void bindPermissions(Long id, BindMenuPermissionsRequest req) {
    menuPermRepo.deleteByMenuId(id);
    if (req.permissionIds != null) {
      for (Long pid : req.permissionIds) {
        SysMenuPermission mp = new SysMenuPermission();
        mp.setMenuId(id);
        mp.setPermissionId(pid);
        menuPermRepo.save(mp);
      }
    }
  }

  private MenuTree toTree(SysMenu m) {
    MenuTree t = new MenuTree();
    t.id = m.getId();
    t.parentId = m.getParentId();
    t.menuType = m.getMenuType();
    t.menuName = m.getMenuName();
    t.routePath = m.getRoutePath();
    t.componentPath = m.getComponentPath();
    t.permissionHint = m.getPermissionHint();
    t.icon = m.getIcon();
    t.useIconPicker = toBool(m.getUseIconPicker());
    t.orderNum = m.getOrderNum();
    t.externalLink = m.getExternalLink();
    t.badgeText = m.getBadgeText();
    t.activePath = m.getActivePath();
    t.enabled = toBool(m.getEnabled());
    t.cachePage = toBool(m.getCachePage());
    t.hiddenMenu = toBool(m.getHiddenMenu());
    t.embedded = toBool(m.getEmbedded());
    t.showBadge = toBool(m.getShowBadge());
    t.affix = toBool(m.getAffix());
    t.hideTab = toBool(m.getHideTab());
    t.fullScreen = toBool(m.getFullScreen());
    // 绑定的权限列表
    for (SysMenuPermission mp : menuPermRepo.findByMenuId(m.getId())) {
      MenuAuth a = new MenuAuth();
      a.id = mp.getPermissionId();
      a.menuId = mp.getMenuId();
      java.util.Optional<SysPermission> p = permRepo.findById(mp.getPermissionId());
      a.authMark = p.map(SysPermission::getPermCode).orElse(String.valueOf(mp.getPermissionId()));
      a.title = p.map(SysPermission::getPermName).orElse(a.authMark);
      t.authList.add(a);
    }
    return t;
  }

  private MenuDetail toDetail(SysMenu m) {
    MenuDetail d = new MenuDetail();
    MenuTree t = toTree(m);
    // copy scalar fields
    d.id = t.id; d.parentId = t.parentId; d.menuType = t.menuType; d.menuName = t.menuName;
    d.routePath = t.routePath; d.componentPath = t.componentPath; d.permissionHint = t.permissionHint;
    d.icon = t.icon; d.useIconPicker = t.useIconPicker; d.orderNum = t.orderNum; d.externalLink = t.externalLink;
    d.badgeText = t.badgeText; d.activePath = t.activePath; d.enabled = t.enabled; d.cachePage = t.cachePage;
    d.hiddenMenu = t.hiddenMenu; d.embedded = t.embedded; d.showBadge = t.showBadge; d.affix = t.affix;
    d.hideTab = t.hideTab; d.fullScreen = t.fullScreen; d.children = t.children;
    return d;
  }

  private SysMenu fromCreate(MenuCreateRequest req) {
    SysMenu m = new SysMenu();
    m.setParentId(req.parentId);
    m.setMenuType(req.menuType != null ? req.menuType : 2);
    m.setMenuName(req.menuName);
    m.setRoutePath(req.routePath);
    m.setComponentPath(req.componentPath);
    m.setPermissionHint(req.permissionHint);
    m.setIcon(req.icon);
    m.setUseIconPicker(fromBool(req.useIconPicker));
    m.setOrderNum(req.orderNum);
    m.setExternalLink(req.externalLink);
    m.setBadgeText(req.badgeText);
    m.setActivePath(req.activePath);
    m.setEnabled(fromBool(req.enabled));
    m.setCachePage(fromBool(req.cachePage));
    m.setHiddenMenu(fromBool(req.hiddenMenu));
    m.setEmbedded(fromBool(req.embedded));
    m.setShowBadge(fromBool(req.showBadge));
    m.setAffix(fromBool(req.affix));
    m.setHideTab(fromBool(req.hideTab));
    m.setFullScreen(fromBool(req.fullScreen));
    LocalDateTime now = LocalDateTime.now();
    m.setCreatedAt(now);
    m.setUpdatedAt(now);
    return m;
  }

  private void applyUpdate(SysMenu m, MenuUpdateRequest req) {
    if (req.parentId != null) m.setParentId(req.parentId);
    if (req.menuType != null) m.setMenuType(req.menuType);
    if (req.menuName != null) m.setMenuName(req.menuName);
    if (req.routePath != null) m.setRoutePath(req.routePath);
    if (req.componentPath != null) m.setComponentPath(req.componentPath);
    if (req.permissionHint != null) m.setPermissionHint(req.permissionHint);
    if (req.icon != null) m.setIcon(req.icon);
    if (req.useIconPicker != null) m.setUseIconPicker(fromBool(req.useIconPicker));
    if (req.orderNum != null) m.setOrderNum(req.orderNum);
    if (req.externalLink != null) m.setExternalLink(req.externalLink);
    if (req.badgeText != null) m.setBadgeText(req.badgeText);
    if (req.activePath != null) m.setActivePath(req.activePath);
    if (req.enabled != null) m.setEnabled(fromBool(req.enabled));
    if (req.cachePage != null) m.setCachePage(fromBool(req.cachePage));
    if (req.hiddenMenu != null) m.setHiddenMenu(fromBool(req.hiddenMenu));
    if (req.embedded != null) m.setEmbedded(fromBool(req.embedded));
    if (req.showBadge != null) m.setShowBadge(fromBool(req.showBadge));
    if (req.affix != null) m.setAffix(fromBool(req.affix));
    if (req.hideTab != null) m.setHideTab(fromBool(req.hideTab));
    if (req.fullScreen != null) m.setFullScreen(fromBool(req.fullScreen));
    m.setUpdatedAt(LocalDateTime.now());
  }

  private void ensureChangeLogMenuExists() {
    final String routePath = "/change/log";
    if (menuRepo.findByRoutePath(routePath).isPresent()) {
      return;
    }
    SysMenu menu = new SysMenu();
    menu.setMenuType(2);
    menu.setMenuName("更新日志");
    menu.setRoutePath(routePath);
    menu.setComponentPath("/change/log");
    menu.setPermissionHint("sys:changelog:list");
    menu.setIcon("&#xe712;");
    menu.setUseIconPicker(0);
    menu.setOrderNum(99);
    menu.setExternalLink(null);
    menu.setBadgeText(null);
    menu.setActivePath("");
    menu.setEnabled(1);
    menu.setCachePage(0);
    menu.setHiddenMenu(0);
    menu.setEmbedded(0);
    menu.setShowBadge(0);
    menu.setAffix(0);
    menu.setHideTab(0);
    menu.setFullScreen(0);
    LocalDateTime now = LocalDateTime.now();
    menu.setCreatedAt(now);
    menu.setUpdatedAt(now);
    menuRepo.save(menu);
  }

  private Boolean toBool(Integer v) { return v != null && v != 0; }
  private Integer fromBool(Boolean v) { return v != null && v ? 1 : 0; }

  @Transactional
  public MenuAuth createAuth(Long menuId, MenuAuthRequest req, Long operatorId) {
    SysMenu menu = menuRepo.findById(menuId).orElseThrow(() -> new RuntimeException("Menu not found"));
    if (req.authMark == null || req.authMark.isEmpty()) throw new RuntimeException("权限标识不能为空");
    if (permRepo.existsByPermCode(req.authMark)) throw new RuntimeException("权限标识已存在");
    SysPermission permission = new SysPermission();
    permission.setPermCode(req.authMark);
    permission.setPermName(req.title != null ? req.title : req.authMark);
    permission.setPermType(3);
    permission.setResource(menu.getRoutePath());
    permission.setAction("BUTTON");
    permission.setEffect(1);
    LocalDateTime now = LocalDateTime.now();
    permission.setCreatedAt(now);
    permission.setUpdatedAt(now);
    permission.setCreatedBy(operatorId);
    permission.setUpdatedBy(operatorId);
    permRepo.save(permission);

    SysMenuPermission mp = new SysMenuPermission();
    mp.setMenuId(menuId);
    mp.setPermissionId(permission.getId());
    menuPermRepo.save(mp);

    MenuAuth auth = new MenuAuth();
    auth.id = permission.getId();
    auth.menuId = menuId;
    auth.authMark = permission.getPermCode();
    auth.title = permission.getPermName();
    return auth;
  }

  @Transactional
  public MenuAuth updateAuth(Long menuId, Long permissionId, MenuAuthRequest req, Long operatorId) {
    menuRepo.findById(menuId).orElseThrow(() -> new RuntimeException("Menu not found"));
    SysPermission permission = permRepo.findById(permissionId).orElseThrow(() -> new RuntimeException("权限不存在"));
    if (req.authMark != null && !req.authMark.equals(permission.getPermCode())) {
      if (permRepo.existsByPermCode(req.authMark)) throw new RuntimeException("权限标识已存在");
      permission.setPermCode(req.authMark);
    }
    if (req.title != null) {
      permission.setPermName(req.title);
    }
    permission.setUpdatedAt(LocalDateTime.now());
    permission.setUpdatedBy(operatorId);
    permRepo.save(permission);
    MenuAuth auth = new MenuAuth();
    auth.id = permission.getId();
    auth.menuId = menuId;
    auth.authMark = permission.getPermCode();
    auth.title = permission.getPermName();
    return auth;
  }

  @Transactional
  public void deleteAuth(Long menuId, Long permissionId) {
    menuRepo.findById(menuId).orElseThrow(() -> new RuntimeException("Menu not found"));
    if (permissionId == null) return;
    menuPermRepo.deleteByMenuIdAndPermissionId(menuId, permissionId);
    removePermissionBinding(permissionId, null);
  }

  private void removePermissionBinding(Long permissionId, Long menuId) {
    if (permissionId == null) return;
    if (menuId != null) {
      menuPermRepo.deleteByMenuIdAndPermissionId(menuId, permissionId);
    }
    if (menuPermRepo.countByPermissionId(permissionId) == 0) {
      menuPermRepo.deleteByPermissionId(permissionId);
      rolePermRepo.deleteByPermissionId(permissionId);
      permRepo.deleteById(permissionId);
    }
  }

  private List<MenuTree> filterMenuTree(List<MenuTree> nodes, Set<Long> allowedMenuIds, Set<Long> allowedPermIds) {
    List<MenuTree> filtered = new ArrayList<>();
    if (nodes == null) {
      return filtered;
    }
    for (MenuTree node : nodes) {
      if (node == null) continue;
      node.children = filterMenuTree(node.children, allowedMenuIds, allowedPermIds);
      if (allowedPermIds != null) {
        node.authList.removeIf(auth -> auth == null || auth.id == null || !allowedPermIds.contains(auth.id));
      }
      boolean hasChildren = node.children != null && !node.children.isEmpty();
      if (allowedMenuIds.contains(node.id) || hasChildren) {
        filtered.add(node);
      }
    }
    return filtered;
  }

  private Set<Long> includeAncestorMenus(Set<Long> menuIds) {
    if (menuIds == null) {
      return new HashSet<>();
    }
    Set<Long> result = new HashSet<>(menuIds);
    if (menuIds.isEmpty()) {
      return result;
    }
    Map<Long, Long> parentMap = menuRepo.findAll().stream()
        .collect(Collectors.toMap(SysMenu::getId, SysMenu::getParentId));
    for (Long id : menuIds) {
      Long parent = parentMap.get(id);
      while (parent != null && result.add(parent)) {
        parent = parentMap.get(parent);
      }
    }
    return result;
  }

  private boolean hasSuperRole(List<Long> roleIds) {
    if (roleIds == null || roleIds.isEmpty()) {
      return false;
    }
    for (SysRole role : roleRepo.findAllById(roleIds)) {
      if (role == null) continue;
      String code = role.getRoleCode();
      if (code == null) continue;
      String normalized = code.trim().toLowerCase();
      if ("r_super".equalsIgnoreCase(code) || "sys_admin".equalsIgnoreCase(code) || "super_admin".equals(normalized)) {
        return true;
      }
    }
    return false;
  }

  // 演示菜单种子（仅当数据库为空时插入）
  @Transactional
  public void seedDemoMenusIfEmpty() {
    if (menuRepo.count() > 0) return;
    java.time.LocalDateTime now = java.time.LocalDateTime.now();
    SysMenu dashboard = new SysMenu();
    dashboard.setMenuType(1); dashboard.setMenuName("仪表盘"); dashboard.setRoutePath("/dashboard");
    dashboard.setOrderNum(1); dashboard.setEnabled(1); dashboard.setCreatedAt(now); dashboard.setUpdatedAt(now);
    menuRepo.save(dashboard);

    SysMenu console = new SysMenu();
    console.setParentId(dashboard.getId()); console.setMenuType(2); console.setMenuName("控制台");
    console.setRoutePath("/dashboard/console"); console.setComponentPath("/dashboard/console");
    console.setOrderNum(1); console.setEnabled(1); console.setCreatedAt(now); console.setUpdatedAt(now);
    menuRepo.save(console);

    SysMenu system = new SysMenu();
    system.setMenuType(1); system.setMenuName("系统设置"); system.setRoutePath("/system");
    system.setOrderNum(2); system.setEnabled(1); system.setCreatedAt(now); system.setUpdatedAt(now);
    menuRepo.save(system);

    SysMenu user = new SysMenu();
    user.setParentId(system.getId()); user.setMenuType(2); user.setMenuName("用户管理");
    user.setRoutePath("/system/user"); user.setComponentPath("/system/user");
    user.setOrderNum(1); user.setEnabled(1); user.setCreatedAt(now); user.setUpdatedAt(now);
    menuRepo.save(user);

    SysMenu role = new SysMenu();
    role.setParentId(system.getId()); role.setMenuType(2); role.setMenuName("角色管理");
    role.setRoutePath("/system/role"); role.setComponentPath("/system/role");
    role.setOrderNum(2); role.setEnabled(1); role.setCreatedAt(now); role.setUpdatedAt(now);
    menuRepo.save(role);

    SysMenu menu = new SysMenu();
    menu.setParentId(system.getId()); menu.setMenuType(2); menu.setMenuName("菜单管理");
    menu.setRoutePath("/system/menu"); menu.setComponentPath("/system/menu");
    menu.setOrderNum(3); menu.setEnabled(1); menu.setCreatedAt(now); menu.setUpdatedAt(now);
    menuRepo.save(menu);
  }
}
