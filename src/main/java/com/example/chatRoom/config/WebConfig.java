package com.example.chatRoom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Assurez-vous que le chemin de l'image est mappé correctement
        registry.addResourceHandler("/img/**").addResourceLocations("classpath:/static/img/");
    }
}
