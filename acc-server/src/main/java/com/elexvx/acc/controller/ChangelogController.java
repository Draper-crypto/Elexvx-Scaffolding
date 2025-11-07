package com.elexvx.acc.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.elexvx.acc.dto.BaseResponse;
import com.elexvx.acc.dto.ChangelogCreateRequest;
import com.elexvx.acc.dto.ChangelogUpdateRequest;
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
import java.util.Map;

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

    @PutMapping("/{id}")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<ChangelogDto>> update(@PathVariable Long id, @RequestBody ChangelogUpdateRequest req) throws Exception {
        return changelogRepository.findById(id)
                .map(c -> {
                    try {
                        if (req.getVersion() != null) c.setVersion(req.getVersion());
                        if (req.getTitle() != null) c.setTitle(req.getTitle());
                        if (req.getDate() != null) c.setDate(req.getDate().isEmpty() ? null : LocalDate.parse(req.getDate()));
                        if (req.getDetail() != null) c.setDetailJson(objectMapper.writeValueAsString(req.getDetail()));
                        if (req.getRequireReLogin() != null) c.setRequireReLogin(req.getRequireReLogin());
                        if (req.getRemark() != null) c.setRemark(req.getRemark());
                        Changelog saved = changelogRepository.save(c);
                        return ResponseEntity.ok(BaseResponse.ok(ChangelogDto.from(saved, objectMapper)));
                    } catch (Exception e) {
                        return ResponseEntity.ok(BaseResponse.<ChangelogDto>error(500, e.getMessage()));
                    }
                })
                .orElseGet(() -> ResponseEntity.ok(BaseResponse.<ChangelogDto>error(404, "记录不存在")));
    }

    @DeleteMapping("/{id}")
    @SaCheckLogin
    public ResponseEntity<BaseResponse<Object>> delete(@PathVariable Long id) {
        if (!changelogRepository.existsById(id)) {
            return ResponseEntity.ok(BaseResponse.error(404, "记录不存在"));
        }
        changelogRepository.deleteById(id);
        return ResponseEntity.ok(BaseResponse.ok(Map.of("deleted", true)));
    }
}
