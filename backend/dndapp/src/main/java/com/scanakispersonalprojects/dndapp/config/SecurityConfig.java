package com.scanakispersonalprojects.dndapp.config;


// import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


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
            .csrf(customizer -> customizer.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .defaultSuccessUrl("/characters/", true)
                .permitAll()
            )
            .httpBasic(Customizer.withDefaults());
            


        return http.build();
    }


    // Password Ecnoder bean
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
