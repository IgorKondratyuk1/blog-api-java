package org.development.blogApi.security.filters;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    @Value("${rate-limit.requestsTTL}")
    private int requestsTTL;

    @Value("${rate-limit.requestsLimit}")
    private int requestsLimit;

    @Value("${rate-limit.enable}")
    private boolean isRateLimitingEnable;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!isRateLimitingEnable) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("RateLimitingFilter");

        // TODO make exclude rate limiting for specific routes
        String userIdentifier = getUserIdentifier(request);
        Bucket bucket = buckets.computeIfAbsent(userIdentifier, key -> {
            Refill refill = Refill.greedy(requestsLimit, Duration.ofSeconds(requestsTTL));
            Bandwidth limit = Bandwidth.classic(requestsLimit, refill);
            return Bucket.builder().addLimit(limit).build();
        });

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.getWriter().write("Too many requests - try again later.");
            return;
        }
    }

    private String getUserIdentifier(HttpServletRequest request) {
        return request.getRemoteAddr(); // Identify the user by IP address or another identifier
    }
}
