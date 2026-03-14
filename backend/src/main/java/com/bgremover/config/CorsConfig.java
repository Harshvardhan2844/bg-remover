package com.bgremover.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * CORS Configuration.
 *
 * CORS (Cross-Origin Resource Sharing) allows our React frontend
 * running on localhost:3000 to make requests to our Spring Boot
 * backend running on localhost:8080.
 *
 * Without this, the browser would block those requests.
 */
@Configuration
public class CorsConfig {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow requests from our React frontend
        config.setAllowedOrigins(Arrays.asList(frontendUrl));

        // Allow all HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers
        config.setAllowedHeaders(Arrays.asList("*"));

        // Allow cookies and Authorization header
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Apply to all routes

        return new CorsFilter(source);
    }
}
