package com.sqli.matchmaking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Autorise toutes les sources (c'est-à-dire, permet à n'importe quelle origine d'effectuer des requêtes)
        config.addAllowedOrigin("*");
        
        // Autorise les méthodes HTTP GET, POST, PUT, DELETE, etc.
        config.addAllowedMethod("*");
        
        // Autorise les en-têtes personnalisés (si nécessaire)
        config.addAllowedHeader("*");
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter((CorsConfigurationSource) source);
    }
}
