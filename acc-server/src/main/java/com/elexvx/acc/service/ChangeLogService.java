package com.elexvx.acc.service;

import com.elexvx.acc.common.PageResponse;
import com.elexvx.acc.dto.ChangeLogDtos.*;
import com.elexvx.acc.entity.SysChangeLog;
import com.elexvx.acc.repo.SysChangeLogRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChangeLogService {
  private final SysChangeLogRepository changeLogRepository;

  public ChangeLogService(SysChangeLogRepository changeLogRepository) {
    this.changeLogRepository = changeLogRepository;
  }

  public PageResponse<ChangeLogItem> list(int page, int size, String keyword, String releaseDate) {
    Pageable pageable = PageRequest.of(Math.max(page - 1, 0), Math.max(size, 1),
        Sort.by(Sort.Direction.DESC, "releaseDate", "createdAt"));
    Specification<SysChangeLog> spec = (root, query, cb) -> {
      jakarta.persistence.criteria.Predicate predicate = cb.conjunction();
      if (keyword != null && !keyword.isEmpty()) {
        String pattern = "%" + keyword.trim() + "%";
        predicate = cb.and(predicate, cb.or(
            cb.like(root.get("title"), pattern),
            cb.like(root.get("version"), pattern),
            cb.like(root.get("remark"), pattern)
        ));
      }
      if (releaseDate != null && !releaseDate.isEmpty()) {
        try {
          LocalDate date = LocalDate.parse(releaseDate);
          predicate = cb.and(predicate, cb.equal(root.get("releaseDate"), date));
        } catch (Exception ignored) {}
      }
      return predicate;
    };
    Page<SysChangeLog> result = changeLogRepository.findAll(spec, pageable);
    List<ChangeLogItem> items = result.getContent().stream().map(this::toItem).collect(Collectors.toList());
    return new PageResponse<>(items, result.getTotalElements(), page, size);
  }

  public List<ChangeLogItem> listPublic() {
    Sort sort = Sort.by(Sort.Direction.DESC, "releaseDate", "createdAt");
    return changeLogRepository.findAll(sort).stream().map(this::toItem).collect(Collectors.toList());
  }

  public ChangeLogItem get(Long id) {
    SysChangeLog entity = changeLogRepository.findById(id).orElseThrow(() -> new RuntimeException("变更日志不存在"));
    return toItem(entity);
  }

  @Transactional
  public ChangeLogItem create(ChangeLogRequest req, Long operatorId) {
    SysChangeLog entity = new SysChangeLog();
    applyRequest(entity, req, operatorId, true);
    changeLogRepository.save(entity);
    return toItem(entity);
  }

  @Transactional
  public ChangeLogItem update(Long id, ChangeLogRequest req, Long operatorId) {
    SysChangeLog entity = changeLogRepository.findById(id).orElseThrow(() -> new RuntimeException("变更日志不存在"));
    applyRequest(entity, req, operatorId, false);
    changeLogRepository.save(entity);
    return toItem(entity);
  }

  @Transactional
  public void delete(Long id) {
    changeLogRepository.deleteById(id);
  }

  private void applyRequest(SysChangeLog entity, ChangeLogRequest req, Long operatorId, boolean isCreate) {
    entity.setVersion(req.version);
    entity.setTitle(req.title);
    entity.setContent(req.content);
    entity.setReleaseDate(req.releaseDate);
    entity.setRemark(req.remark);
    entity.setRequireReLogin(Boolean.TRUE.equals(req.requireReLogin) ? 1 : 0);
    LocalDateTime now = LocalDateTime.now();
    if (isCreate) {
      entity.setCreatedAt(now);
      entity.setCreatedBy(operatorId);
    }
    entity.setUpdatedAt(now);
    entity.setUpdatedBy(operatorId);
  }

  private ChangeLogItem toItem(SysChangeLog entity) {
    ChangeLogItem item = new ChangeLogItem();
    item.id = entity.getId();
    item.version = entity.getVersion();
    item.title = entity.getTitle();
    item.content = entity.getContent();
    item.releaseDate = entity.getReleaseDate();
    item.remark = entity.getRemark();
    item.requireReLogin = entity.getRequireReLogin() != null && entity.getRequireReLogin() == 1;
    item.createdAt = entity.getCreatedAt();
    item.updatedAt = entity.getUpdatedAt();
    return item;
  }
}
