package org.development.blogApi.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.development.blogApi.modules.auth.exceptions.AuthException;
import org.development.blogApi.security.CustomUserDetails;
import org.development.blogApi.modules.securityDevice.exceptions.SecurityDeviceNotFoundException;
import org.development.blogApi.security.JwtService;
import org.development.blogApi.modules.securityDevice.SecurityDeviceService;
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

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SecurityDeviceService securityDeviceService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info("Jwt Strict Auth Filter");

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String usernameOrEmail;
        final String userId;
        final String deviceId;
        final LocalDateTime lastActiveDate;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("Token is not found");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        if (jwtService.isTokenExpired(jwt)) {
            log.info("Token is expired");
            filterChain.doFilter(request, response);
            return;
        }

        usernameOrEmail = jwtService.extractLogin(jwt);
        userId = jwtService.extractUserId(jwt);
        deviceId = jwtService.extractDeviceId(jwt);
        lastActiveDate = jwtService.extractLastActiveDate(jwt);

        if (usernameOrEmail == null || userId == null) {
            log.info("Token data is not valid");
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            log.info("SecurityContextHolder is not empty:\n" + SecurityContextHolder.getContext().getAuthentication().getName());
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails;
//        try {
            userDetails = this.userDetailsService.loadUserByUsername(usernameOrEmail);
//        } catch (Exception e) {
//            log.error("UserDetails error:\n", e);
//            filterChain.doFilter(request, response);
//            return;
//        }

        CustomUserDetails customUserDetails = new CustomUserDetails(userDetails, userId, deviceId, lastActiveDate);

        if (!jwtService.isTokenValid(jwt, userDetails)) {
            log.info("Token is not valid");
            filterChain.doFilter(request, response);
            return;
        }

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
    }
}
