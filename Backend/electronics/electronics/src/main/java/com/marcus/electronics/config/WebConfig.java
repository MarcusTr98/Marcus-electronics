package com.marcus.electronics.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Khi gọi http://localhost:8080/images/products/abc.jpg
        // Nó sẽ tự tìm trong C:/marcus_uploads/products/
        registry.addResourceHandler("/images/products/**")
                .addResourceLocations("file:///C:/marcus_uploads/products/");
    }
}