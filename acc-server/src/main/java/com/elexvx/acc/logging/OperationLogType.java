package com.elexvx.acc.logging;

public enum OperationLogType {
  LOGIN("登录", "#52c41a", "success"),
  LOGOUT("登出", "#2f54eb", "info"),
  CREATE("新增", "#1890ff", "success"),
  UPDATE("修改", "#faad14", "warning"),
  DELETE("删除", "#ff4d4f", "danger"),
  QUERY("查询", "#13c2c2", "info"),
  PLUGIN_LOAD("载入插件", "#722ed1", "success"),
  PLUGIN_UNLOAD("卸载插件", "#d46b08", "warning"),
  SETTING("系统设置", "#2f54eb", "info"),
  OTHER("其他", "#595959", "info");

  private final String label;
  private final String colorHex;
  private final String tagType;

  OperationLogType(String label, String colorHex, String tagType) {
    this.label = label;
    this.colorHex = colorHex;
    this.tagType = tagType;
  }

  public String getLabel() {
    return label;
  }

  public String getColorHex() {
    return colorHex;
  }

  public String getTagType() {
    return tagType;
  }

  public static OperationLogType fromValue(String value) {
    if (value == null) {
      return OperationLogType.OTHER;
    }
    for (OperationLogType type : OperationLogType.values()) {
      if (type.name().equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown OperationLogType: " + value);
  }
}
