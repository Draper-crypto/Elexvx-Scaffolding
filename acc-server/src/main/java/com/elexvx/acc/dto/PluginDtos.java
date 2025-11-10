package com.elexvx.acc.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PluginDtos {
  public static class PluginInfo {
    public String pluginKey;
    public String name;
    public String version;
    public String description;
    public String status;
    public boolean installed;
    public boolean available;
    public String pluginPath;
    public String manifestPath;
    public String backendEntry;
    public String frontendEntry;
    public List<String> databaseScripts;
    public String dataPath;
    public LocalDateTime installedAt;
    public LocalDateTime updatedAt;
    public LocalDateTime lastLoadedAt;
    public LocalDateTime lastUnloadedAt;
    public String manifestError;
  }

  public static class PluginLoadRequest {
    public String pluginKey;
  }
}
