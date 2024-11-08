package com.backend.thesis.config;

import com.backend.thesis.service.UserService;
import com.backend.thesis.utility.other.RequestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final UserService userService;

    public WebConfig(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new RequestInterceptor(this.userService));
    }
}
