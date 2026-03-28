package com.marcus.electronics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Cho phép frontend Vue (dev) gọi vào — đổi thành domain thật khi lên
        // production
        config.addAllowedOrigin("http://localhost:5173"); // Vite dev server
        config.addAllowedOrigin("http://localhost:3000"); // Dự phòng nếu dùng port khác

        // Cho phép tất cả HTTP method mà CRUD cần
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS"); // Bắt buộc phải có — browser gửi preflight request trước

        // Cho phép header Authorization (JWT) và Content-Type
        config.addAllowedHeader("Authorization");
        config.addAllowedHeader("Content-Type");

        // Cho phép gửi cookie / token trong request (cần cho JWT)
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config); // Áp dụng cho toàn bộ /api/

        return new CorsFilter(source);
    }
}