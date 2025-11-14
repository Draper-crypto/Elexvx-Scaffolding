package com.elexvx.scaffold.dto;

public class ForgotPasswordRequest {
    private String username;
    private String newPassword;
    private String captchaToken;
    private String captchaCode;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getCaptchaToken() { return captchaToken; }
    public void setCaptchaToken(String captchaToken) { this.captchaToken = captchaToken; }
    public String getCaptchaCode() { return captchaCode; }
    public void setCaptchaCode(String captchaCode) { this.captchaCode = captchaCode; }
}
