package com.hey.givumethemoney.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // WebMvcConfigurer를 필요한 경우만 확장하고, 별도 설정이 없다면 빈 클래스로 유지
}
