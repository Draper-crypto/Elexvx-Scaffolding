package com.elexvx.acc.dto;

public class SystemSettingDtos {
  public static class BrandSetting {
    public String name;
    public String logoUrl;
  }

  public static class WatermarkSetting {
    public Boolean enabled;
    public String mode; // username | custom
    public String customText;
    public Integer fontSize;
  }

  public static class SystemSettingResponse {
    public BrandSetting brand;
    public WatermarkSetting watermark;
  }

  public static class UpdateBrandRequest {
    public String name;
    public String logoUrl;
  }

  public static class UpdateWatermarkRequest {
    public Boolean enabled;
    public String mode;
    public String customText;
    public Integer fontSize;
  }
}
