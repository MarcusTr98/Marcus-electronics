package com.marcus.electronics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Tắt CSRF (để Postman gọi được mà không bị chặn)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Cấu hình quyền truy cập
                .authorizeHttpRequests(auth -> auth
                        // Cho phép TẤT CẢ các request đi qua (không cần đăng nhập)
                        .anyRequest().permitAll());

        return http.build();
    }
}