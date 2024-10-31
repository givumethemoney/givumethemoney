package com.hey.givumethemoney.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
    class PasswordConfig {
         @Bean
         public PasswordEncoder passwordEncoder() {
             return new BCryptPasswordEncoder(); // 비밀번호 암호화를 위한 PasswordEncoder 빈 등록
         }
}
