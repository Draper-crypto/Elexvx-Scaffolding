package com.elexvx.acc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CaptchaService {
    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    public static class Entry {
        public String code;
        public long expireAt;
        public Entry(String code, long expireAt) { this.code = code; this.expireAt = expireAt; }
    }

    public record Captcha(String token, String base64, int expireSec) {}

    public Captcha createCaptcha(int width, int height, int expireSec) {
        String code = randomCode(4);
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        long expireAt = Instant.now().getEpochSecond() + expireSec;
        store.put(token, new Entry(code, expireAt));

        String base64 = generateImageBase64(code, width, height);
        return new Captcha(token, base64, expireSec);
    }

    public boolean verify(String token, String code) {
        if (token == null || code == null) return false;
        Entry e = store.get(token);
        if (e == null) return false;
        if (Instant.now().getEpochSecond() > e.expireAt) { store.remove(token); return false; }
        boolean ok = e.code.equalsIgnoreCase(code);
        store.remove(token);
        return ok;
    }

    private String randomCode(int n) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int idx = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }

    private String generateImageBase64(String code, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(new Color(30, 144, 255));
        g.setFont(new Font("Arial", Font.BOLD, height - 10));
        g.drawString(code, 10, height - 10);
        g.dispose();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            return "";
        }
    }
}

