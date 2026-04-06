package com.education.education.config;

import com.education.education.auth.filters.JwtAuthenticationFilter;
import com.education.education.auth.filters.JwtAuthorizationFilter;
import com.education.education.auth.utils.AuthUtils;
import com.education.education.user.user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;

    @Bean
public CorsConfigurationSource corsConfigurationSource() {  // ← correct type and name
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of("*"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;  // ← return the source, not a CorsFilter wrapper
}


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, 
            AuthenticationConfiguration authenticationConfiguration,
            AuthUtils authUtils,
            UserRepository userRepository
    ) throws Exception {
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        http.cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                           auth -> auth
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Explicitly permit preflight requests
                                .anyRequest().permitAll()
                );
        http.sessionManagement(
                s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        http.addFilter(new JwtAuthenticationFilter(authenticationManager, authUtils, userRepository));
        http.addFilterBefore(new JwtAuthorizationFilter(authUtils, userDetailsService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}