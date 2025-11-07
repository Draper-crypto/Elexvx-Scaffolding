package com.elexvx.acc.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.elexvx.acc.dto.BaseResponse;
import com.elexvx.acc.dto.PaginatedResponse;
import com.elexvx.acc.dto.admin.RoleCreateRequest;
import com.elexvx.acc.model.Role;
import com.elexvx.acc.repository.RoleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {
    private final RoleRepository roleRepository;

    @GetMapping
    @SaCheckLogin
    public ResponseEntity<BaseResponse<PaginatedResponse<Role>>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<Role> all = roleRepository.findAll();
        int total = all.size();
        int from = Math.max((page - 1) * size, 0);
        int to = Math.min(from + size, total);
        List<Role> slice = from < to ? all.subList(from, to) : Collections.emptyList();
        PaginatedResponse<Role> resp = new PaginatedResponse<>(slice, page, size, total);
        return ResponseEntity.ok(BaseResponse.ok(resp));
    }

    @GetMapping("/{id}")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<Role>> detail(@PathVariable Long id) {
        return roleRepository.findById(id)
                .map(r -> ResponseEntity.ok(BaseResponse.ok(r)))
                .orElseGet(() -> ResponseEntity.ok(BaseResponse.error(404, "角色不存在")));
    }

    @PostMapping
    @SaCheckLogin
    public ResponseEntity<BaseResponse<Role>> create(@Valid @RequestBody RoleCreateRequest req) {
        Role r = Role.builder()
                .roleName(req.getRoleName())
                .roleCode(req.getRoleCode())
                .description(req.getDescription())
                .status(Optional.ofNullable(req.getStatus()).orElse(1))
                .build();
        Role saved = roleRepository.save(r);
        return ResponseEntity.ok(BaseResponse.ok(saved));
    }

    @PutMapping("/{id}")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<Role>> update(@PathVariable Long id, @Valid @RequestBody RoleCreateRequest req) {
        Optional<Role> opt = roleRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.ok(BaseResponse.error(404, "角色不存在"));
        Role r = opt.get();
        if (req.getRoleName() != null) r.setRoleName(req.getRoleName());
        if (req.getRoleCode() != null) r.setRoleCode(req.getRoleCode());
        if (req.getDescription() != null) r.setDescription(req.getDescription());
        if (req.getStatus() != null) r.setStatus(req.getStatus());
        Role saved = roleRepository.save(r);
        return ResponseEntity.ok(BaseResponse.ok(saved));
    }

    @DeleteMapping("/{id}")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<Map<String, Object>>> delete(@PathVariable Long id) {
        if (!roleRepository.existsById(id)) {
            return ResponseEntity.ok(BaseResponse.error(404, "角色不存在"));
        }
        roleRepository.deleteById(id);
        return ResponseEntity.ok(BaseResponse.ok(Map.of("deleted", true)));
    }
}

