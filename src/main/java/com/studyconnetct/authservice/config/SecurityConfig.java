package com.studyconnetct.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Security Configuration para Spring Boot 4 con Spring Security 6+
 * Configuración moderna usando lambda style sin WebSecurityConfigurerAdapter
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * Configura el SecurityFilterChain con reglas de autorización y CORS
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Configurar CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Deshabilitar CSRF (típico en APIs REST stateless)
            .csrf(csrf -> csrf.disable())
            
            // Configuración de sesión (stateless para JWT)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configurar autorización de endpoints
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos para autenticación y registro
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/health").permitAll()
                .requestMatchers("/api/auth/refresh").permitAll()
                .requestMatchers("/api/auth/validate-token").permitAll()
                .requestMatchers("/api/auth/logout/**").permitAll()
                // Proteger el resto
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
    
    /**
     * Bean para encriptación de contraseñas usando BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Configuración de CORS integrada en Spring Security
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Orígenes permitidos
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:8080",
            "http://localhost:8000"
        ));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Exponer headers específicos si es necesario
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type"
        ));
        
        // Permitir credenciales
        configuration.setAllowCredentials(true);
        
        // Tiempo máximo de caché en segundos (1 hora)
        configuration.setMaxAge(3600L);
        
        // Registrar configuración de CORS
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
