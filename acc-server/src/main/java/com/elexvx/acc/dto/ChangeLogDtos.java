package com.elexvx.acc.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ChangeLogDtos {
  public static class ChangeLogItem {
    public Long id;
    public String version;
    public String title;
    public String content;
    public LocalDate releaseDate;
    public String remark;
    public Boolean requireReLogin;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
  }

  public static class ChangeLogRequest {
    public String version;
    public String title;
    public String content;
    public LocalDate releaseDate;
    public String remark;
    public Boolean requireReLogin;
  }
}
