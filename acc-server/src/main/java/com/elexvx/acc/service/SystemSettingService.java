package com.elexvx.acc.service;

import com.elexvx.acc.dto.SystemSettingDtos.*;
import com.elexvx.acc.entity.SysGlobalSetting;
import com.elexvx.acc.repo.SysGlobalSettingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SystemSettingService {
  private static final String BRAND_KEY = "brand.name";
  private static final String WATERMARK_KEY = "ui.watermark";
  private static final String DEFAULT_BRAND = "Art Design Pro";

  private final SysGlobalSettingRepository settingRepository;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public SystemSettingService(SysGlobalSettingRepository settingRepository) {
    this.settingRepository = settingRepository;
  }

  public SystemSettingResponse getPublicSettings() {
    SystemSettingResponse resp = new SystemSettingResponse();
    resp.brand = getBrandSetting();
    resp.watermark = getWatermarkSetting();
    return resp;
  }

  public SystemSettingResponse getAdminSettings() {
    return getPublicSettings();
  }

  @Transactional
  public BrandSetting updateBrand(UpdateBrandRequest req, Long operatorId) {
    BrandSetting payload = new BrandSetting();
    payload.name = (req.name == null || req.name.isBlank()) ? DEFAULT_BRAND : req.name.trim();
    upsertSetting(BRAND_KEY, payload, operatorId, "品牌配置");
    return payload;
  }

  @Transactional
  public WatermarkSetting updateWatermark(UpdateWatermarkRequest req, Long operatorId) {
    WatermarkSetting payload = new WatermarkSetting();
    payload.enabled = req.enabled != null ? req.enabled : Boolean.FALSE;
    String mode = (req.mode == null || req.mode.isBlank()) ? "username" : req.mode.trim().toLowerCase();
    if (!mode.equals("custom")) {
      mode = "username";
    }
    payload.mode = mode;
    payload.customText = req.customText == null ? "" : req.customText.trim();
    payload.fontSize = (req.fontSize != null && req.fontSize > 8) ? req.fontSize : 16;
    upsertSetting(WATERMARK_KEY, payload, operatorId, "水印配置");
    return payload;
  }

  private BrandSetting getBrandSetting() {
    return settingRepository.findBySettingKey(BRAND_KEY)
        .map(entity -> readValue(entity, BrandSetting.class))
        .orElseGet(() -> {
          BrandSetting setting = new BrandSetting();
          setting.name = DEFAULT_BRAND;
          return setting;
        });
  }

  private WatermarkSetting getWatermarkSetting() {
    return settingRepository.findBySettingKey(WATERMARK_KEY)
        .map(entity -> readValue(entity, WatermarkSetting.class))
        .orElseGet(() -> {
          WatermarkSetting setting = new WatermarkSetting();
          setting.enabled = Boolean.TRUE;
          setting.mode = "username";
          setting.customText = "";
          setting.fontSize = 16;
          return setting;
        });
  }

  private <T> T readValue(SysGlobalSetting entity, Class<T> type) {
    try {
      if (entity.getSettingValue() == null || entity.getSettingValue().isEmpty()) {
        return type.getDeclaredConstructor().newInstance();
      }
      return objectMapper.readValue(entity.getSettingValue(), type);
    } catch (Exception e) {
      throw new RuntimeException("解析全局设置失败", e);
    }
  }

  private void upsertSetting(String key, Object payload, Long operatorId, String description) {
    try {
      SysGlobalSetting entity = settingRepository.findBySettingKey(key).orElseGet(SysGlobalSetting::new);
      entity.setSettingKey(key);
      entity.setDescription(description);
      entity.setSettingValue(objectMapper.writeValueAsString(payload));
      LocalDateTime now = LocalDateTime.now();
      if (entity.getCreatedAt() == null) {
        entity.setCreatedAt(now);
        entity.setCreatedBy(operatorId);
      }
      entity.setUpdatedAt(now);
      entity.setUpdatedBy(operatorId);
      settingRepository.save(entity);
    } catch (Exception e) {
      throw new RuntimeException("更新全局设置失败", e);
    }
  }
}
