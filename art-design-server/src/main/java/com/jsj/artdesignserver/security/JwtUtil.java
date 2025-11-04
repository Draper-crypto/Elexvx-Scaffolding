package com.jsj.artdesignserver.security;

import com.jsj.artdesignserver.entity.SysPermission;
import com.jsj.artdesignserver.entity.SysRole;
import com.jsj.artdesignserver.entity.SysUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-seconds}")
    private long expirationSeconds;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(ensureBase64(secret));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String ensureBase64(String s) {
        // 为方便配置，若不是 Base64，则直接按原文字节编码后再 Base64
        try {
            Decoders.BASE64.decode(s);
            return s; // 已是 Base64
        } catch (Exception ignored) {
            return java.util.Base64.getEncoder().encodeToString(s.getBytes());
        }
    }

    public String generateToken(SysUser user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationSeconds);

        Set<String> roles = user.getRoles().stream().map(SysRole::getRoleCode).collect(Collectors.toSet());
        Set<String> perms = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(SysPermission::getPermCode)
                .collect(Collectors.toSet());

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("roles", roles)
                .claim("permissions", perms)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

