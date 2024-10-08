package org.development.blogApi.security.config;

import org.development.blogApi.security.filters.JwtAccessSoftAuthFilter;
import org.development.blogApi.security.filters.JwtAccessAuthFilter;
import org.development.blogApi.security.filters.JwtRefreshAuthFilter;
import org.development.blogApi.security.filters.RateLimitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FiltersConfig {

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateFilter(RateLimitFilter rateLimitingFilter) {
        FilterRegistrationBean<RateLimitFilter> registrationBean = new FilterRegistrationBean<>(rateLimitingFilter);
        registrationBean.setEnabled(true);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registrationBean.addUrlPatterns(
                "/api/auth/login",
                "/api/auth/registration",
                "/api/auth/registration-confirmation",
                "/api/auth/registration-email-resending",
                "/api/auth/new-password",
                "/api/auth/password-recovery",

                "/api/blogger/blogs",
                "/api/blogger/blogs/*",

                "/api/comments",
                "/api/comments/*",

                "/api/posts",
                "/api/posts/*"
        );

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<JwtAccessSoftAuthFilter> jwtAccessSoftFilter(JwtAccessSoftAuthFilter jwtAuthFilter) {
        FilterRegistrationBean<JwtAccessSoftAuthFilter> registrationBean = new FilterRegistrationBean<>(jwtAuthFilter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<JwtRefreshAuthFilter> jwtRefreshFilter(JwtRefreshAuthFilter jwtAuthFilter) {
        FilterRegistrationBean<JwtRefreshAuthFilter> registrationBean = new FilterRegistrationBean<>(jwtAuthFilter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<JwtAccessAuthFilter> jwtAccessStrictFilter(JwtAccessAuthFilter jwtAuthFilter) {
        FilterRegistrationBean<JwtAccessAuthFilter> registrationBean = new FilterRegistrationBean<>(jwtAuthFilter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }
}
