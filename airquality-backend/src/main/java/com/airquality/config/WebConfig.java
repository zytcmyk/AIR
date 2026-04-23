package com.airquality.config;

import com.airquality.interceptor.JwtInterceptor;
import com.airquality.interceptor.RoleInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private RoleInterceptor roleInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JWT拦截器
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除登录注册接口
                .excludePathPatterns(
                        "/api/auth/**",
                        "/api/article/**",
                        "/error",
                        "/doc.html",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/v2/**"
                );

        // 角色权限拦截器（在JWT拦截器之后执行）
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/auth/**",
                        "/api/article/**",
                        "/error",
                        "/doc.html",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/v2/**"
                );
    }
}
