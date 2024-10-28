package org.development.blogApi.infrastructure.security.annotation;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.development.blogApi.infrastructure.security.CustomUserDetails;
import org.development.blogApi.infrastructure.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class GetUserFromJwtAspect {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HttpServletRequest request;

    @Autowired
    public GetUserFromJwtAspect(JwtService jwtService, UserDetailsService userDetailsService, HttpServletRequest request) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.request = request;
    }

    @Before("@annotation(org.development.blogApi.infrastructure.security.annotation.GetUserFromJwt)")
    public void processJwtUser() {
        try {
            log.info("GetUserFromJwt");

            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String usernameOrEmail;
            final String userId;
            final String deviceId;
            final LocalDateTime lastActiveDate;

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return;
            }

            jwt = authHeader.substring(7);
            usernameOrEmail = jwtService.extractLogin(jwt);
            userId = jwtService.extractUserId(jwt);
            deviceId = jwtService.extractDeviceId(jwt);
            lastActiveDate = jwtService.extractLastActiveDate(jwt);

            if (usernameOrEmail == null || userId == null) {
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                log.info("GetUserFromJwt SecurityContextHolder is not empty:\n" + SecurityContextHolder.getContext().getAuthentication().getName());
                return;
            }

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(usernameOrEmail);
            CustomUserDetails customUserDetails = new CustomUserDetails(userDetails, userId, deviceId, lastActiveDate);

            if (!jwtService.isTokenValid(jwt, userDetails)) {
                return;
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    customUserDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e) {
            log.error("GetUserFromJwt Exception:", e);
        }
    }
}
