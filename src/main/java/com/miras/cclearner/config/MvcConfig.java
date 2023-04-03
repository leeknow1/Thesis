package com.miras.cclearner.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${files.char.path}")
    private String charPath;

    @Value("${files.category.path}")
    private String categoryPath;

    @Value("${files.request.path}")
    private String requestPath;

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/api/login").setViewName("login");
        registry.addViewController("/access-denied").setViewName("custom403");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/categories/**").addResourceLocations("file:///" + categoryPath);
        registry.addResourceHandler("/characters/**").addResourceLocations("file:///" + charPath);
        registry.addResourceHandler("/requests/**").addResourceLocations("file:///" + requestPath);
    }
}
