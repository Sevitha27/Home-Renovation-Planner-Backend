package com.lowes.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Public API GET Endpoints to be Added here
    private final String[] PUBLIC_GET_ENDPOINTS = {
        "/phase/**","/api/enums/phase-statuses","/phase/phases/by-renovation-type/**","/api/**",
            "/api/vendor-reviews/by-phaseType","/api/vendor-reviews/available-vendors"
    };

    // Public API POST Endpoints to be Added here
    private final String[] PUBLIC_POST_ENDPOINTS = {
            "/auth/register", "/auth/login", "/auth/refreshAccessToken","/phase/**","/api/**"
    };

//For testing without authentication: you may uncomment the required methods below as needed.

    // Public API DELETE Endpoints to be Added here
//    private final String[] PUBLIC_DELETE_ENDPOINTS = {
//        "/phase"
//    };
//
//    // Public API PUT Endpoints to be Added here
//    private final String[] PUBLIC_PUT_ENDPOINTS = {
//        "/phase"
//    };
//
//    // Public API PATCH Endpoints to be Added here
//    private final String[] PUBLIC_PATCH_ENDPOINTS = {
//
//    };

    private final List<String> ALLOWED_ORIGIN_URI = List.of(
            "http://localhost:5173", "http://localhost:5174"
    );

    private final String[] ALLOWED_CORS_METHODS = {
            "GET", "POST", "PUT", "PATCH", "DELETE"
    };

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ALLOWED_ORIGIN_URI);
        configuration.setAllowedMethods(List.of(ALLOWED_CORS_METHODS));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf( csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                                .requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS).permitAll()

//For testing without Authentication: you may uncomment the required methods below as needed.
//                        .requestMatchers(HttpMethod.DELETE, PUBLIC_DELETE_ENDPOINTS).permitAll()
//                        .requestMatchers(HttpMethod.PATCH, PUBLIC_PATCH_ENDPOINTS).permitAll()
//                        .requestMatchers(HttpMethod.PUT, PUBLIC_PUT_ENDPOINTS).permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}