package com.jsj.artdesignserver.controller;

import com.jsj.artdesignserver.dto.PaginatedResponse;
import com.jsj.artdesignserver.dto.SystemManageRoleItem;
import com.jsj.artdesignserver.entity.SysRole;
import com.jsj.artdesignserver.repository.SysRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final SysRoleRepository roleRepo;

    @GetMapping("/list")
    public ResponseEntity<PaginatedResponse<SystemManageRoleItem>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size
    ) {
        int pageIndex = Math.max(current - 1, 0);
        Page<SysRole> page = roleRepo.findAll(PageRequest.of(pageIndex, size, Sort.by(Sort.Direction.DESC, "id")));

        List<SystemManageRoleItem> records = page.getContent().stream()
                .map(SystemManageRoleItem::from)
                .collect(Collectors.toList());

        PaginatedResponse<SystemManageRoleItem> resp = PaginatedResponse.<SystemManageRoleItem>builder()
                .records(records)
                .current(current)
                .size(size)
                .total(page.getTotalElements())
                .build();
        return ResponseEntity.ok(resp);
    }
}
