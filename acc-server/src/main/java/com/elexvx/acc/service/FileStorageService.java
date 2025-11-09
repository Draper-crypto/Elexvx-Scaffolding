package com.elexvx.acc.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
  private final Path rootLocation;

  public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
    this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    try {
      Files.createDirectories(this.rootLocation);
    } catch (IOException e) {
      throw new RuntimeException("无法创建上传目录", e);
    }
  }

  public String store(MultipartFile file, String subDirectory) {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("上传文件不能为空");
    }
    String filename = StringUtils.cleanPath(file.getOriginalFilename());
    String extension = "";
    int dotIndex = filename.lastIndexOf('.');
    if (dotIndex >= 0) {
      extension = filename.substring(dotIndex);
    }
    String targetName = UUID.randomUUID().toString() + extension;
    Path targetDir = rootLocation.resolve(subDirectory).normalize();
    try {
      Files.createDirectories(targetDir);
      Path target = targetDir.resolve(targetName);
      Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("保存文件失败", e);
    }
    return "/uploads/" + subDirectory + "/" + targetName;
  }

  public String getResourceLocation() {
    return rootLocation.toUri().toString();
  }
}
