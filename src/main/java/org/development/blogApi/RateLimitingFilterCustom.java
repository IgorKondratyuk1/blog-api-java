package org.development.blogApi;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Filter;

//@Component
public class RateLimitingFilterCustom extends OncePerRequestFilter {
    private final RateLimiterRegistry rateLimiterRegistry;
    private final ConcurrentHashMap<String, RateLimiter> rateLimiterCache = new ConcurrentHashMap<>();
    @Value("${rate-limit.requestsLimit}")
    private int requestLimit;
    @Value("${rate-limit.requestsTTL}")
    private int requestsTTL;
    @Value("${rate-limit.timeout}")
    private int timeoutInMs;

    public RateLimitingFilterCustom(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = extractClientIp(request);
        String device = extractDeviceName(request);
        String userKey = ip + "-" + device; // Unique key based on IP and device
        RateLimiter rateLimiter = rateLimiterCache.computeIfAbsent(userKey, this::createRateLimiterForUser);
        if (!rateLimiter.acquirePermission()) {
            // If rate limit is exceeded, return 429 status and stop request processing
            response.setStatus(429);
            response.getWriter().write("Too many requests, please try again later. " + userKey);
            return;
        }
        // Proceed with the request if the rate limit is not exceeded
        filterChain.doFilter(request, response);
    }
    private RateLimiter createRateLimiterForUser(String userKey) {
        System.out.println(requestLimit);
        System.out.println(requestsTTL);
        System.out.println(timeoutInMs);

        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(requestLimit)                               // Allow requests as per property
                .limitRefreshPeriod(Duration.ofMillis(requestsTTL))  // Refresh period from property
                .timeoutDuration(Duration.ofMillis(timeoutInMs))      // Timeout from property
                .build();
        return rateLimiterRegistry.rateLimiter(userKey, config);
    }
    private String extractClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }
    private String extractDeviceName(HttpServletRequest request) {
        return request.getHeader("User-Agent");  // Assuming device name is passed as User-Agent
    }
}