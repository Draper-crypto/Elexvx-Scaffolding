package com.elexvx.acc.config;

import com.elexvx.acc.model.Changelog;
import com.elexvx.acc.repository.ChangelogRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ChangelogDataInitializer {
    private final ChangelogRepository changelogRepository;

    @PostConstruct
    public void init() {
        if (changelogRepository.count() == 0) {
            changelogRepository.save(Changelog.builder()
                    .version("v1.0.0")
                    .title("初始化系统，新增更新日志存储")
                    .date(LocalDate.now())
                    .detailJson("[\"后端新增 Changelog 实体与接口\",\"前端支持从接口显示更新日志\"]")
                    .requireReLogin(false)
                    .remark("示例数据，便于前端展示与验证")
                    .build());
        }
    }
}

