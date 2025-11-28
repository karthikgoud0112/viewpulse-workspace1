package com.viewpulse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceConfig implements WebMvcConfigurer {

    // Read the path from application.properties
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ensure the path ends with a slash and has the "file:" prefix
        String path = "file:" + uploadDir + "/";
        
        // Serve uploaded videos from the configured directory
        registry.addResourceHandler("/uploads/videos/**")
                .addResourceLocations(path);
        
        System.out.println("📁 Static resources configured: /uploads/videos/ -> " + path);
    }
}