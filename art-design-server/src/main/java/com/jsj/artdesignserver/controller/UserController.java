package com.jsj.artdesignserver.controller;

import com.jsj.artdesignserver.dto.PaginatedResponse;
import com.jsj.artdesignserver.dto.SystemManageUserItem;
import com.jsj.artdesignserver.entity.SysUser;
import com.jsj.artdesignserver.repository.SysUserRepository;
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
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final SysUserRepository userRepo;

    @GetMapping("/list")
    public ResponseEntity<PaginatedResponse<SystemManageUserItem>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size
    ) {
        int pageIndex = Math.max(current - 1, 0);
        Page<SysUser> page = userRepo.findAll(PageRequest.of(pageIndex, size, Sort.by(Sort.Direction.DESC, "id")));

        List<SystemManageUserItem> records = page.getContent().stream()
                .map(SystemManageUserItem::from)
                .collect(Collectors.toList());

        PaginatedResponse<SystemManageUserItem> resp = PaginatedResponse.<SystemManageUserItem>builder()
                .records(records)
                .current(current)
                .size(size)
                .total(page.getTotalElements())
                .build();
        return ResponseEntity.ok(resp);
    }
}

