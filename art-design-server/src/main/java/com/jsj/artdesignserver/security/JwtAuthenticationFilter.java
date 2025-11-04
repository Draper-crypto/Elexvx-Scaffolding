package com.jsj.artdesignserver.security;

import com.jsj.artdesignserver.repository.SysUserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final SysUserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, SysUserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = jwtUtil.parse(token);
                Long userId = Long.parseLong(claims.getSubject());
                Optional<com.jsj.artdesignserver.entity.SysUser> optUser = userRepository.findById(userId);
                if (optUser.isPresent()) {
                    @SuppressWarnings("unchecked") List<String> perms = (List<String>) claims.get("permissions", List.class);
                    var authorities = perms == null ? List.<SimpleGrantedAuthority>of() :
                            perms.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception ignored) {
                // 无效 token 不设置认证，交由后续处理
            }
        }
        filterChain.doFilter(request, response);
    }
}

