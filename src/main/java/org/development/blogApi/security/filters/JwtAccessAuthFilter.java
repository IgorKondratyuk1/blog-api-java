package org.development.blogApi.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.development.blogApi.exceptions.authExceptions.AuthException;
import org.development.blogApi.exceptions.securityDeviceExceptions.SecurityDeviceNotFoundException;
import org.development.blogApi.security.CustomUserDetails;
import org.development.blogApi.security.JwtService;
import org.development.blogApi.securityDevice.SecurityDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.time.LocalDateTime;

@Slf4j
@Component
public class JwtAccessAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SecurityDeviceService securityDeviceService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    public JwtAccessAuthFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver,
                               JwtService jwtService,
                               SecurityDeviceService securityDeviceService,
                               UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.securityDeviceService = securityDeviceService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {

        try {
            log.info("Jwt Strict Auth Filter");

            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String usernameOrEmail;
            final String userId;
            final String deviceId;
            final LocalDateTime lastActiveDate;

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new AuthException("Token is not found");
            }

            jwt = authHeader.substring(7);

            if (jwtService.isTokenExpired(jwt)) {
                throw new AuthException("Token is expired");
            }

            usernameOrEmail = jwtService.extractLogin(jwt);
            userId = jwtService.extractUserId(jwt);
            deviceId = jwtService.extractDeviceId(jwt);
            lastActiveDate = jwtService.extractLastActiveDate(jwt);

            if (usernameOrEmail == null || userId == null) {
                throw new AuthException("Token data is not valid");
            }

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                log.info("SecurityContextHolder is not empty:\n" + SecurityContextHolder.getContext().getAuthentication().getName());
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(usernameOrEmail);
            CustomUserDetails customUserDetails = new CustomUserDetails(userDetails, userId, deviceId, lastActiveDate);

            if (!jwtService.isTokenValid(jwt, userDetails)) {
                throw new AuthException("Token is not valid");
            }

            // Search security device due to device session management (connect with logout)
            try {
                this.securityDeviceService.findDeviceSessionByDeviceId(deviceId);
            } catch (SecurityDeviceNotFoundException notFoundException) {
                throw new AuthException(notFoundException.getMessage());
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    customUserDetails,
                    null,
                    userDetails.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Jwt Strict Auth Filter Chain Exception:", e);
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
