package com.elexvx.scaffold.dto;

public class CaptchaResponse {
    private String captchaToken;
    private String imageBase64;
    private int expireInSeconds;

    public String getCaptchaToken() { return captchaToken; }
    public void setCaptchaToken(String captchaToken) { this.captchaToken = captchaToken; }
    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
    public int getExpireInSeconds() { return expireInSeconds; }
    public void setExpireInSeconds(int expireInSeconds) { this.expireInSeconds = expireInSeconds; }
}

