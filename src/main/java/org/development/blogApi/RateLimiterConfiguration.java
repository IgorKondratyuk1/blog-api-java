package org.development.blogApi;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

//@Configuration
public class RateLimiterConfiguration {
    @Value("${rate-limit.requestsLimit}")
    private int requests;
    @Value("${rate-limit.requestsTTL}")
    private long periodInMillis;
    @Value("${rate-limit.timeout}")
    private long timeoutInMillis;

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        System.out.println(requests);
        System.out.println(periodInMillis);
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(requests)
                .limitRefreshPeriod(Duration.ofMillis(periodInMillis))   // Millisecond precision
                .timeoutDuration(Duration.ofMillis(timeoutInMillis))     // Millisecond precision
                .build();
        return RateLimiterRegistry.of(config);
    }
}
