package org.development.blogApi.infrastructure.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${rate-limit.requestsLimit}")
    private int requestsLimit;

    @Value("${rate-limit.requestsTTL}")
    private int requestsTTL;

    @Value("${rate-limit.enable}")
    private Boolean enableRateLimit;

    private final Map<String, RequestAttempt> attemptsMap = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!enableRateLimit) {
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("Rate Limiting Filter Custom");

        String ip = getClientIp(request);
        String url = request.getRequestURI();
        String key = ip + "-" + url;

        // Check if the rate limit is exceeded
        if (isRateLimitExceeded(key)) {
            response.setStatus(429); // 429 status
            response.getWriter().write("Too many requests, please try again later.");
            return;
        }

        // Log the new request attempt
        setNewAttempt(key);

        // Continue the request processing
        filterChain.doFilter(request, response);
    }

    private boolean isRateLimitExceeded(String key) {
        RequestAttempt attempt = attemptsMap.get(key);

        if (attempt == null) {
            return false;
        }

        long currentTime = new Date().getTime();
        // Check if the time window has passed
        if (currentTime - attempt.firstAttemptTime > requestsTTL) {
            attemptsMap.remove(key); // Reset the attempts after the time window
            return false;
        }

        // Check if the max attempts have been reached
        return attempt.count >= requestsLimit;
    }

    private void setNewAttempt(String key) {
        long currentTime = new Date().getTime();
        attemptsMap.compute(key, (k, attempt) -> {
            if (attempt == null || currentTime - attempt.firstAttemptTime > requestsTTL) {
                // Reset attempts if it's a new key or time window expired
                return new RequestAttempt(1, currentTime);
            } else {
                // Increment attempt count
                attempt.count++;
                return attempt;
            }
        });
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    // Inner class to track attempts
    private static class RequestAttempt {
        int count;
        long firstAttemptTime;

        public RequestAttempt(int count, long firstAttemptTime) {
            this.count = count;
            this.firstAttemptTime = firstAttemptTime;
        }
    }
}