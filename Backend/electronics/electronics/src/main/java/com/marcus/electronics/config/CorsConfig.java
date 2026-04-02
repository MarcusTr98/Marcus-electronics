// package com.marcus.electronics.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.web.filter.CorsFilter;

// @Configuration
// public class CorsConfig {

// @Bean
// public CorsFilter corsFilter() {
// CorsConfiguration config = new CorsConfiguration();

// // 1. Chỉ định chính xác Domain được phép (Bắt buộc khi có Credentials)
// config.addAllowedOrigin("http://localhost:5173");
// config.addAllowedOrigin("http://localhost:3000");

// // 2. Cho phép TẤT CẢ các HTTP Method (GET, POST, PUT, PATCH, DELETE,
// // OPTIONS...)
// config.addAllowedMethod("*");

// // 3. Cho phép TẤT CẢ các Header
// config.addAllowedHeader("*");

// // 4. Cho phép gửi Cookie/Token
// config.setAllowCredentials(true);

// UrlBasedCorsConfigurationSource source = new
// UrlBasedCorsConfigurationSource();
// source.registerCorsConfiguration("/**", config);

// return new CorsFilter(source);
// }
// }