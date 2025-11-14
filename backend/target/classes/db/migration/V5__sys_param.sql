CREATE TABLE IF NOT EXISTS sys_param (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  param_key VARCHAR(100) NOT NULL UNIQUE,
  param_value VARCHAR(1000) NULL
);

INSERT INTO sys_param(param_key,param_value) VALUES('watermark.mode','username')
ON DUPLICATE KEY UPDATE param_value=VALUES(param_value);

INSERT INTO sys_param(param_key,param_value) VALUES('watermark.custom','')
ON DUPLICATE KEY UPDATE param_value=VALUES(param_value);