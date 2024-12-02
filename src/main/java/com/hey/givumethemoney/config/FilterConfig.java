package com.hey.givumethemoney.Config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hey.givumethemoney.jwt.JWTFilter;

@Configuration
public class FilterConfig {

    private final JWTFilter jwtFilter;

    public FilterConfig(JWTFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public FilterRegistrationBean<JWTFilter> jwtFilterRegistration() {
        FilterRegistrationBean<JWTFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtFilter);
        registrationBean.addUrlPatterns("/api/*");  // /api/로 시작하는 URL 패턴에 대해서만 필터 적용
        return registrationBean;
    }
}

