package org.development.blogApi.security.filters;

import org.development.blogApi.logs.LoggingFilter;
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
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
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
    public FilterRegistrationBean<LoggingFilter> logFilter(LoggingFilter loggingFilter) {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>(loggingFilter);
        registrationBean.setEnabled(true);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
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
