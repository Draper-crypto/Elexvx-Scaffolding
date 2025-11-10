package com.elexvx.acc.entity;

import com.elexvx.acc.enums.PluginStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_plugin")
public class SysPlugin {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "plugin_key", length = 100, nullable = false, unique = true)
  private String pluginKey;

  @Column(name = "plugin_name", length = 255, nullable = false)
  private String pluginName;

  @Column(name = "version", length = 50)
  private String version;

  @Column(name = "description", length = 500)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 30, nullable = false)
  private PluginStatus status;

  @Column(name = "plugin_path", length = 500)
  private String pluginPath;

  @Column(name = "manifest_path", length = 500)
  private String manifestPath;

  @Column(name = "backend_entry", length = 255)
  private String backendEntry;

  @Column(name = "frontend_entry", length = 255)
  private String frontendEntry;

  @Column(name = "database_scripts", columnDefinition = "text")
  private String databaseScripts;

  @Column(name = "data_path", length = 500)
  private String dataPath;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "installed_at")
  private LocalDateTime installedAt;

  @Column(name = "last_loaded_at")
  private LocalDateTime lastLoadedAt;

  @Column(name = "last_unloaded_at")
  private LocalDateTime lastUnloadedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getPluginKey() {
    return pluginKey;
  }

  public void setPluginKey(String pluginKey) {
    this.pluginKey = pluginKey;
  }

  public String getPluginName() {
    return pluginName;
  }

  public void setPluginName(String pluginName) {
    this.pluginName = pluginName;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public PluginStatus getStatus() {
    return status;
  }

  public void setStatus(PluginStatus status) {
    this.status = status;
  }

  public String getPluginPath() {
    return pluginPath;
  }

  public void setPluginPath(String pluginPath) {
    this.pluginPath = pluginPath;
  }

  public String getManifestPath() {
    return manifestPath;
  }

  public void setManifestPath(String manifestPath) {
    this.manifestPath = manifestPath;
  }

  public String getBackendEntry() {
    return backendEntry;
  }

  public void setBackendEntry(String backendEntry) {
    this.backendEntry = backendEntry;
  }

  public String getFrontendEntry() {
    return frontendEntry;
  }

  public void setFrontendEntry(String frontendEntry) {
    this.frontendEntry = frontendEntry;
  }

  public String getDatabaseScripts() {
    return databaseScripts;
  }

  public void setDatabaseScripts(String databaseScripts) {
    this.databaseScripts = databaseScripts;
  }

  public String getDataPath() {
    return dataPath;
  }

  public void setDataPath(String dataPath) {
    this.dataPath = dataPath;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public LocalDateTime getInstalledAt() {
    return installedAt;
  }

  public void setInstalledAt(LocalDateTime installedAt) {
    this.installedAt = installedAt;
  }

  public LocalDateTime getLastLoadedAt() {
    return lastLoadedAt;
  }

  public void setLastLoadedAt(LocalDateTime lastLoadedAt) {
    this.lastLoadedAt = lastLoadedAt;
  }

  public LocalDateTime getLastUnloadedAt() {
    return lastUnloadedAt;
  }

  public void setLastUnloadedAt(LocalDateTime lastUnloadedAt) {
    this.lastUnloadedAt = lastUnloadedAt;
  }
}
