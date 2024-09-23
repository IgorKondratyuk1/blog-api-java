package org.development.blogApi.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.development.blogApi.exceptions.authExceprion.AuthException;
import org.development.blogApi.exceptions.userExceptions.UserNotFoundException;
import org.development.blogApi.security.CustomUserDetails;
import org.development.blogApi.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

//    @Autowired
//    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    public JwtAuthFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver,
                         JwtService jwtService,
                         UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {

        try {
            log.info("JwtAuthFilter");

            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String usernameOrEmail;
            final String userId;
            final String deviceId;
            final LocalDateTime lastActiveDate;

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                throw new AuthException("Token is not found");
                filterChain.doFilter(request, response);
                return;
            }

            jwt = authHeader.substring(7);
            usernameOrEmail = jwtService.extractLogin(jwt);
            userId = jwtService.extractUserId(jwt);
            deviceId = jwtService.extractDeviceId(jwt);
            lastActiveDate = jwtService.extractLastActiveDate(jwt);

            if (usernameOrEmail == null || userId == null) {
//                throw new AuthException("User is not found");
                filterChain.doFilter(request, response);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                log.info("SecurityContextHolder is not empty:\n" + SecurityContextHolder.getContext().getAuthentication().getName());
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(usernameOrEmail);
            CustomUserDetails customUserDetails = new CustomUserDetails(userDetails, userId, deviceId, lastActiveDate);

            if(!jwtService.isTokenValid(jwt, userDetails)) {
//                throw new AuthException("Token is not valid");
                filterChain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    customUserDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Spring Security Filter Chain Exception:", e);
            handlerExceptionResolver.resolveException(request, response, null, e);
        }

    }
}
