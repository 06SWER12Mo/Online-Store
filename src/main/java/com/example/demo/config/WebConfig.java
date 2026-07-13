package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.image.util.ImageConstants;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        // ============================================================
        // 1. SERVE IMAGES FROM FILE SYSTEM (YOUR STORE-IMAGES FOLDER)
        // ============================================================
        // This maps /api/images/** to C:\Users\Dell\OneDrive\Desktop\store-images\
        // Example: /api/images/products/1/main.jpg 
        //          → C:\Users\Dell\OneDrive\Desktop\store-images\products\1\main.jpg
        // ============================================================
        registry.addResourceHandler("/api/images/**")
                .addResourceLocations("file:" + ImageConstants.BASE_PATH + "/");
        
        // ============================================================
        // 2. LEGACY UPLOADS (If you have any)
        // ============================================================
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");

        // ============================================================
        // 3. STATIC RESOURCES (Classpath)
        // ============================================================
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
        
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
    }
}