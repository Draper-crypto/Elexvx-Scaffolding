-- Seed default users with BCrypt hashed password '123456'
INSERT INTO sys_user (username, password_hash, name, status)
VALUES
('Super', '$2a$10$7EqJtq98hPqEX7fNLEG5OuE7h.uGcLWXh0qzQfM8hPvLT3T1/4A0e', '超级管理员', 1),
('Admin', '$2a$10$7EqJtq98hPqEX7fNLEG5OuE7h.uGcLWXh0qzQfM8hPvLT3T1/4A0e', '管理员', 1),
('User', '$2a$10$7EqJtq98hPqEX7fNLEG5OuE7h.uGcLWXh0qzQfM8hPvLT3T1/4A0e', '普通用户', 1);

-- Bind roles: 1=ADMIN, 2=USER
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, 1 FROM sys_user u WHERE u.username IN ('Super','Admin');

INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, 2 FROM sys_user u WHERE u.username = 'User';
