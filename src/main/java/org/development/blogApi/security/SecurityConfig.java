package org.development.blogApi.security;

import org.development.blogApi.security.exceptionHandlers.CustomAuthenticationEntryPoint;
import org.development.blogApi.security.filters.JwtAccessSoftAuthFilter;
import org.development.blogApi.security.filters.JwtAccessAuthFilter;
import org.development.blogApi.security.filters.JwtRefreshAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableAsync
public class SecurityConfig {
    private final CustomBasicAuthProvider customBasicAuthProvider;
    private final JwtAccessAuthFilter jwtAccessAuthFilter;
    private final JwtAccessSoftAuthFilter jwtAccessSoftAuthFilter;
    private final JwtRefreshAuthFilter jwtRefreshAuthFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired
    public SecurityConfig(JwtAccessAuthFilter jwtAccessAuthFilter,
                          JwtAccessSoftAuthFilter jwtAccessSoftAuthFilter,
                          JwtRefreshAuthFilter jwtRefreshAuthFilter,
                          CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                          CustomBasicAuthProvider customBasicAuthProvider) {
        this.jwtAccessAuthFilter = jwtAccessAuthFilter;
        this.jwtAccessSoftAuthFilter = jwtAccessSoftAuthFilter;
        this.jwtRefreshAuthFilter = jwtRefreshAuthFilter;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customBasicAuthProvider = customBasicAuthProvider;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain basicFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/actuator/**", "/api/sa/**")
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(customBasicAuthProvider.basicAuthProvider());

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain jwtRefreshFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/api/auth/refresh-token",
                        "/api/auth/logout",
                        "/api/security/devices/**")
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/refresh-token",
                                "/api/auth/logout",
                                "/api/security/devices/**").authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRefreshAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(customAuthenticationEntryPoint));

        return http.build();
    }

//    @Bean
//    @Order(2)
//    public SecurityFilterChain jwtAccessSoftFilterChain(HttpSecurity http) throws Exception {
//        http
//                .securityMatcher(
//                        "/api/blogs/*/posts",
//                        "/api/comments/*",
//                        "/api/posts",
//                        "/api/posts/*",
//                        "/api/posts/*/comments")
//                .csrf(csrf -> csrf.disable())
//                .cors(cors -> cors.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(HttpMethod.GET, "/api/blogs/*/posts").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/comments/*").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/posts", "/api/posts/*").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/posts/*/comments").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/posts/*/comments").permitAll()
//                )
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(jwtAccessSoftAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
//                        httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(customAuthenticationEntryPoint));
//
//        return http.build();
//    }

    @Bean
    @Order(3)
    public SecurityFilterChain jwtAccessStrictFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/api/auth/me",
                        "/api/posts/*/comments",
                        "/api/posts/*/like-status",
                        "/api/blogger/**",
                        "/api/comments/**")
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/posts/*/comments").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/comments/*").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAccessAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(customAuthenticationEntryPoint));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

