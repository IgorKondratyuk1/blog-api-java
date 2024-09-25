package org.development.blogApi;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class CustomRateLimiterAspect {
    private final RateLimiterRegistry rateLimiterRegistry;
    private final ConcurrentHashMap<String, RateLimiter> rateLimiterCache = new ConcurrentHashMap<>();
    private final HttpServletRequest httpServletRequest;
    public CustomRateLimiterAspect(RateLimiterRegistry rateLimiterRegistry, HttpServletRequest httpServletRequest) {
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.httpServletRequest = httpServletRequest;
    }
    @Pointcut("@annotation(io.github.resilience4j.ratelimiter.annotation.RateLimiter)")
    public void rateLimiterPointcut() {
        // Pointcut to intercept methods annotated with @RateLimiter
    }
    @Before("rateLimiterPointcut()")
    public void applyRateLimiter() {
        System.out.println("CustomRateLimiterAspect");
        String ip = extractClientIp(httpServletRequest);
        String device = extractDeviceName(httpServletRequest);
        String userKey = ip + "-" + device; // Unique key based on IP and device
        RateLimiter rateLimiter = rateLimiterCache.computeIfAbsent(userKey, k -> rateLimiterRegistry.rateLimiter(k));
        // Check if the request exceeds the rate limit
        if (!rateLimiter.acquirePermission()) {
            throw new RateLimitExceededException("Too many requests from " + userKey);
        }
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
