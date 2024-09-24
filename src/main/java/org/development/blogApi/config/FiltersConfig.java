package org.development.blogApi.config;

import org.development.blogApi.security.filters.JwtAccessSoftAuthFilter;
import org.development.blogApi.security.filters.JwtAccessStrictAuthFilter;
import org.development.blogApi.security.filters.JwtRefreshAuthFilter;
import org.development.blogApi.security.filters.RateLimitingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FiltersConfig {

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateFilter(RateLimitingFilter rateLimitingFilter) {
        FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>(rateLimitingFilter);
//        registrationBean.setEnabled(true);
//        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setEnabled(false);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
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
    public FilterRegistrationBean<JwtAccessStrictAuthFilter> jwtAccessStrictFilter(JwtAccessStrictAuthFilter jwtAuthFilter) {
        FilterRegistrationBean<JwtAccessStrictAuthFilter> registrationBean = new FilterRegistrationBean<>(jwtAuthFilter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }
}
