package com.elexvx.acc.dto;

import com.elexvx.acc.model.Changelog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Data
public class ChangelogDto {
    private Long id;
    private String version;
    private String title;
    private LocalDate date;
    private List<String> detail;
    private Boolean requireReLogin;
    private String remark;

    public static ChangelogDto from(Changelog c, ObjectMapper mapper) {
        ChangelogDto dto = new ChangelogDto();
        dto.setId(c.getId());
        dto.setVersion(c.getVersion());
        dto.setTitle(c.getTitle());
        dto.setDate(c.getDate());
        dto.setRequireReLogin(c.getRequireReLogin());
        dto.setRemark(c.getRemark());
        try {
            List<String> d = c.getDetailJson() == null || c.getDetailJson().isEmpty()
                    ? Collections.emptyList()
                    : mapper.readValue(c.getDetailJson(), new TypeReference<List<String>>() {});
            dto.setDetail(d);
        } catch (Exception e) {
            dto.setDetail(Collections.emptyList());
        }
        return dto;
    }
}

