package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.elexvx.acc.dto.BaseResponse;
import com.elexvx.acc.dto.ChangelogCreateRequest;
import com.elexvx.acc.dto.ChangelogDto;
import com.elexvx.acc.model.Changelog;
import com.elexvx.acc.repository.ChangelogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/changelog")
@RequiredArgsConstructor
public class ChangelogController {
    private final ChangelogRepository changelogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    @SaCheckLogin
    public ResponseEntity<BaseResponse<List<ChangelogDto>>> list() {
        List<ChangelogDto> logs = changelogRepository.findAll().stream()
                .sorted(Comparator.comparing(Changelog::getDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(c -> ChangelogDto.from(c, objectMapper))
                .toList();
        return ResponseEntity.ok(BaseResponse.ok(logs));
    }

    @PostMapping
    @SaCheckLogin
    public ResponseEntity<BaseResponse<ChangelogDto>> create(@RequestBody ChangelogCreateRequest req) throws Exception {
        Changelog c = Changelog.builder()
                .version(req.getVersion())
                .title(req.getTitle())
                .date(req.getDate() == null || req.getDate().isEmpty() ? null : LocalDate.parse(req.getDate()))
                .detailJson(req.getDetail() == null ? null : objectMapper.writeValueAsString(req.getDetail()))
                .requireReLogin(req.getRequireReLogin())
                .remark(req.getRemark())
                .build();
        Changelog saved = changelogRepository.save(c);
        return ResponseEntity.ok(BaseResponse.ok(ChangelogDto.from(saved, objectMapper)));
    }
}

