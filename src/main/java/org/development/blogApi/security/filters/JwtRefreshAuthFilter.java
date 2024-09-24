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
import org.development.blogApi.securityDevice.entity.SecurityDevice;
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
public class JwtRefreshAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SecurityDeviceService securityDeviceService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    public JwtRefreshAuthFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver,
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
            log.info("Jwt Refresh Auth Filter");

            final String usernameOrEmail;
            final String userId;
            final String deviceId;
            final LocalDateTime lastActiveDate;
            final String refreshToken = this.jwtService.getJwtRefreshFromCookies(request);

            if (refreshToken == null || refreshToken.isEmpty()) {
                throw new AuthException("Refresh token is not found");
            }

            if (jwtService.isTokenExpired(refreshToken)) {
                throw new AuthException("Refresh token is expired");
            }

            usernameOrEmail = jwtService.extractLogin(refreshToken);
            userId = jwtService.extractUserId(refreshToken);
            deviceId = jwtService.extractDeviceId(refreshToken);
            lastActiveDate = jwtService.extractLastActiveDate(refreshToken);

            if (usernameOrEmail == null || userId == null) {
                throw new AuthException("Refresh token data is not valid");
            }

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(usernameOrEmail);
            CustomUserDetails customUserDetails = new CustomUserDetails(userDetails, userId, deviceId, lastActiveDate);

            if (!jwtService.isTokenValid(refreshToken, userDetails)) {
                throw new AuthException("Refresh token is not valid");
            }

            // Search security device due to device session management (connect with logout)
            SecurityDevice securityDevice;
            try {
                securityDevice = this.securityDeviceService.findDeviceSessionByDeviceId(deviceId);
            } catch (SecurityDeviceNotFoundException notFoundException) {
                throw new AuthException(notFoundException.getMessage());
            }

            // Check that token is valid (lastActiveDate use like unique value)
            if (!securityDevice.getLastActiveDate().isEqual(lastActiveDate)) {
                throw new AuthException("Wrong activation date");
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
            log.error("Jwt Refresh Auth Filter Chain Exception:", e);
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
