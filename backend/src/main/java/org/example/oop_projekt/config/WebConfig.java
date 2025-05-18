package org.example.oop_projekt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry){
        corsRegistry.addMapping("/**")
                .allowedOrigins("https://159.223.29.45",
                                "https://ostukäru.ee", "https://www.ostukäru.ee",
                                "https://xn--ostukru-9wa.ee", "https://www.xn--ostukru-9wa.ee")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowCredentials(true);
    }
}
