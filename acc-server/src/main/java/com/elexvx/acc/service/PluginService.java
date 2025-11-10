package com.elexvx.acc.service;

import com.elexvx.acc.dto.PluginDtos.PluginInfo;
import com.elexvx.acc.entity.SysPlugin;
import com.elexvx.acc.enums.PluginStatus;
import com.elexvx.acc.repo.SysPluginRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class PluginService {
  private static final String MANIFEST_FILE = "plugin.json";
  private static final String STATUS_NOT_INSTALLED = "NOT_INSTALLED";

  private final SysPluginRepository pluginRepository;
  private final ObjectMapper objectMapper;
  private final Path pluginRoot;

  public PluginService(SysPluginRepository pluginRepository,
                       ObjectMapper objectMapper,
                       @Value("${app.plugin-root:plugins}") String pluginRootDir) {
    this.pluginRepository = pluginRepository;
    this.objectMapper = objectMapper;
    this.pluginRoot = Paths.get(pluginRootDir).toAbsolutePath().normalize();
    try {
      Files.createDirectories(this.pluginRoot);
    } catch (IOException e) {
      throw new RuntimeException("无法创建插件目录: " + this.pluginRoot, e);
    }
  }

  public List<PluginInfo> list() {
    Map<String, PluginInfo> infoMap = new LinkedHashMap<>();

    if (Files.exists(pluginRoot) && Files.isDirectory(pluginRoot)) {
      try (Stream<Path> stream = Files.list(pluginRoot)) {
        stream.filter(Files::isDirectory)
            .forEach(dir -> {
              ManifestResult manifestResult = readManifest(dir);
              PluginInfo info = new PluginInfo();
              String key = manifestResult.manifest != null && manifestResult.manifest.key != null
                  ? manifestResult.manifest.key
                  : dir.getFileName().toString();
              info.pluginKey = key;
              info.name = manifestResult.manifest != null && manifestResult.manifest.name != null
                  ? manifestResult.manifest.name
                  : key;
              info.version = manifestResult.manifest != null ? manifestResult.manifest.version : null;
              info.description = manifestResult.manifest != null ? manifestResult.manifest.description : null;
              info.backendEntry = manifestResult.manifest != null ? manifestResult.manifest.backendEntry : null;
              info.frontendEntry = manifestResult.manifest != null ? manifestResult.manifest.frontendEntry : null;
              info.databaseScripts = manifestResult.manifest != null ? manifestResult.manifest.databaseScripts : List.of();
              info.dataPath = manifestResult.manifest != null && manifestResult.manifest.dataPath != null
                  ? manifestResult.manifest.dataPath
                  : dir.resolve("data").toString();
              info.manifestPath = dir.resolve(MANIFEST_FILE).toString();
              info.pluginPath = dir.toString();
              info.available = manifestResult.manifest != null;
              info.installed = false;
              info.status = STATUS_NOT_INSTALLED;
              info.manifestError = manifestResult.error;
              infoMap.put(key, info);
            });
      } catch (IOException e) {
        throw new RuntimeException("读取插件目录失败", e);
      }
    }

    List<SysPlugin> records = pluginRepository.findAll();
    for (SysPlugin record : records) {
      PluginInfo info = infoMap.computeIfAbsent(record.getPluginKey(), key -> {
        PluginInfo pi = new PluginInfo();
        pi.pluginKey = key;
        pi.available = false;
        return pi;
      });
      info.installed = record.getStatus() == PluginStatus.ENABLED;
      info.status = record.getStatus().name();
      info.name = firstNonBlank(info.name, record.getPluginName(), info.pluginKey);
      info.version = firstNonBlank(info.version, record.getVersion());
      info.description = firstNonBlank(info.description, record.getDescription());
      info.pluginPath = firstNonBlank(info.pluginPath, record.getPluginPath());
      info.manifestPath = firstNonBlank(info.manifestPath, record.getManifestPath());
      info.backendEntry = firstNonBlank(info.backendEntry, record.getBackendEntry());
      info.frontendEntry = firstNonBlank(info.frontendEntry, record.getFrontendEntry());
      info.databaseScripts = !isNullOrEmpty(info.databaseScripts)
          ? info.databaseScripts
          : deserializeDatabaseScripts(record.getDatabaseScripts());
      info.dataPath = firstNonBlank(info.dataPath, record.getDataPath());
      info.installedAt = record.getInstalledAt();
      info.updatedAt = record.getUpdatedAt();
      info.lastLoadedAt = record.getLastLoadedAt();
      info.lastUnloadedAt = record.getLastUnloadedAt();
    }

    return new ArrayList<>(infoMap.values());
  }

  public PluginInfo load(String pluginKey) {
    Path pluginDir = pluginRoot.resolve(pluginKey).normalize();
    if (!Files.exists(pluginDir) || !Files.isDirectory(pluginDir)) {
      throw new RuntimeException("未找到插件目录: " + pluginKey);
    }
    ManifestResult manifestResult = readManifest(pluginDir);
    if (manifestResult.manifest == null) {
      throw new RuntimeException("插件缺少有效的 manifest 文件: " + manifestResult.error);
    }
    if (manifestResult.manifest.key != null && !manifestResult.manifest.key.equals(pluginKey)) {
      throw new RuntimeException("插件清单中的 key 与目录名称不一致");
    }

    SysPlugin plugin = pluginRepository.findByPluginKey(pluginKey).orElseGet(SysPlugin::new);
    LocalDateTime now = LocalDateTime.now();
    if (plugin.getId() == null) {
      plugin.setCreatedAt(now);
      plugin.setPluginKey(pluginKey);
    }
    plugin.setPluginName(firstNonBlank(manifestResult.manifest.name, plugin.getPluginName(), pluginKey));
    plugin.setVersion(firstNonBlank(manifestResult.manifest.version, plugin.getVersion()));
    plugin.setDescription(firstNonBlank(manifestResult.manifest.description, plugin.getDescription()));
    plugin.setStatus(PluginStatus.ENABLED);
    plugin.setPluginPath(pluginDir.toString());
    plugin.setManifestPath(pluginDir.resolve(MANIFEST_FILE).toString());
    plugin.setBackendEntry(manifestResult.manifest.backendEntry);
    plugin.setFrontendEntry(manifestResult.manifest.frontendEntry);
    plugin.setDatabaseScripts(serializeDatabaseScripts(manifestResult.manifest.databaseScripts));
    String dataPath = manifestResult.manifest.dataPath != null
        ? manifestResult.manifest.dataPath
        : pluginDir.resolve("data").toString();
    plugin.setDataPath(dataPath);
    plugin.setUpdatedAt(now);
    if (plugin.getInstalledAt() == null) {
      plugin.setInstalledAt(now);
    }
    plugin.setLastLoadedAt(now);
    pluginRepository.save(plugin);

    ensureDirectoryExists(Paths.get(dataPath));

    return buildInfo(pluginKey);
  }

  public PluginInfo unload(String pluginKey) {
    SysPlugin plugin = pluginRepository.findByPluginKey(pluginKey)
        .orElseThrow(() -> new RuntimeException("插件未载入"));
    plugin.setStatus(PluginStatus.DISABLED);
    LocalDateTime now = LocalDateTime.now();
    plugin.setUpdatedAt(now);
    plugin.setLastUnloadedAt(now);
    pluginRepository.save(plugin);
    return buildInfo(pluginKey);
  }

  private PluginInfo buildInfo(String pluginKey) {
    List<PluginInfo> infos = list();
    return infos.stream()
        .filter(info -> pluginKey.equals(info.pluginKey))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("未找到插件信息"));
  }

  private ManifestResult readManifest(Path pluginDir) {
    Path manifestPath = pluginDir.resolve(MANIFEST_FILE);
    if (!Files.exists(manifestPath)) {
      return new ManifestResult(null, "missing manifest file");
    }
    try {
      byte[] content = Files.readAllBytes(manifestPath);
      PluginManifest manifest = objectMapper.readValue(content, PluginManifest.class);
      if (manifest.key == null || manifest.key.isBlank()) {
        manifest.key = pluginDir.getFileName().toString();
      }
      if (manifest.databaseScripts == null) {
        manifest.databaseScripts = List.of();
      }
      if (manifest.dataPath == null || manifest.dataPath.isBlank()) {
        manifest.dataPath = pluginDir.resolve("data").toString();
      }
      return new ManifestResult(manifest, null);
    } catch (IOException e) {
      return new ManifestResult(null, e.getMessage());
    }
  }

  private void ensureDirectoryExists(Path path) {
    if (path == null) {
      return;
    }
    try {
      Files.createDirectories(path);
    } catch (IOException e) {
      throw new RuntimeException("无法创建插件数据目录: " + path, e);
    }
  }

  private String serializeDatabaseScripts(List<String> scripts) {
    if (scripts == null || scripts.isEmpty()) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(scripts);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("序列化插件数据库脚本失败", e);
    }
  }

  private List<String> deserializeDatabaseScripts(String data) {
    if (data == null || data.isBlank()) {
      return List.of();
    }
    try {
      return objectMapper.readValue(data, new TypeReference<List<String>>() {});
    } catch (IOException e) {
      return List.of();
    }
  }

  private String firstNonBlank(String... values) {
    if (values == null) {
      return null;
    }
    for (String value : values) {
      if (value != null && !value.isBlank()) {
        return value;
      }
    }
    return null;
  }

  private boolean isNullOrEmpty(List<String> list) {
    return list == null || list.isEmpty();
  }

  private static class ManifestResult {
    private final PluginManifest manifest;
    private final String error;

    private ManifestResult(PluginManifest manifest, String error) {
      this.manifest = manifest;
      this.error = error;
    }
  }

  private static class PluginManifest {
    public String key;
    public String name;
    public String version;
    public String description;
    public String backendEntry;
    public String frontendEntry;
    public List<String> databaseScripts;
    public String dataPath;
  }
}
