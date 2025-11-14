package com.elexvx.scaffold.service;

import com.elexvx.scaffold.dto.CaptchaResponse;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaptchaService {
    private final Map<String, Entry> store = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    private static class Entry { int answer; long expireAt; }

    public CaptchaResponse generate() {
        int a = random.nextInt(9) + 1;
        int b = random.nextInt(9) + 1;
        int ans = a + b;
        String svg = "<svg xmlns='http://www.w3.org/2000/svg' width='120' height='40'>"+
                "<rect width='120' height='40' fill='#f0f0f0'/><text x='10' y='26' font-size='20' fill='#333'>"+a+"+"+b+"=?"+"</text></svg>";
        String token = randomToken();
        Entry e = new Entry();
        e.answer = ans; e.expireAt = System.currentTimeMillis() + 180_000;
        store.put(token, e);
        CaptchaResponse resp = new CaptchaResponse();
        resp.setCaptchaToken(token);
        resp.setImageBase64("data:image/svg+xml;base64,"+ Base64.getEncoder().encodeToString(svg.getBytes()));
        resp.setExpireInSeconds(180);
        return resp;
    }

    public boolean verify(String token, String code) {
        Entry e = store.get(token);
        if (e == null || System.currentTimeMillis() > e.expireAt) return false;
        try {
            int c = Integer.parseInt(code);
            return c == e.answer;
        } catch (Exception ex) {
            return false;
        } finally {
            store.remove(token);
        }
    }

    private String randomToken() {
        byte[] buf = new byte[16];
        random.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }
}

