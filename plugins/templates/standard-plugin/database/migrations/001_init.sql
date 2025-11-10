-- 示例：插件自定义数据表
-- 可根据实际需求修改或拆分

CREATE TABLE IF NOT EXISTS `plugin_example_table` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `business_key` VARCHAR(64) NOT NULL,
  `payload` JSON NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_example_business_key` (`business_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
