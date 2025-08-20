package com.scanakispersonalprojects.dndapp.config;


import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


/**
 * 
 * Basic Spring-Security setup for the application
 * 
 * - CSRF protection is disabled
 * - Static assets are publicly accesible.
 * - Everythhing else requires authentication
 * - Supports both from login and HTTP basic.
 * - Uses Spring's "delegating" password ecnoder
 * 
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {



    /**
     * Defines main security features
     * 
     * @param http http the fluent security filter chain.
     * @return the fully-built {@link SecruityFilterChain}
     * 
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(customizer -> customizer.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // Add this line!
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginProcessingUrl("/api/auth/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
                    response.setStatus(200);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": true}");
                })
                .failureHandler((request, response, exception) -> {
                    response.setStatus(401);
                    response.getWriter().write("{\"success\": false, \"error\": \"Invalid credentials\"}");
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(200);
                    response.getWriter().write("{\"success\": true}");
                })
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }


    @Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    // Add both development and Docker origins
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:4200",  // Angular dev server
        "http://localhost",        // Docker frontend (port 80),
        "http://54.80.122.64"        // EC2 public IP
        
    ));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}

    // Password Ecnoder bean
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
