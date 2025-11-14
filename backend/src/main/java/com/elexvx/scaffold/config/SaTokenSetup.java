package com.elexvx.scaffold.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenSetup implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
                    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                    if (attr != null && "OPTIONS".equalsIgnoreCase(attr.getRequest().getMethod())) {
                        return; // 预检请求不做登录校验
                    }
                    StpUtil.checkLogin();
                }))
                .addPathPatterns("/**")
                .excludePathPatterns("/api/auth/**");
    }
}
