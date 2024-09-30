package org.development.blogApi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.development.blogApi.user.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;


@Service
public class JwtService {

    private String secretKey;
    private boolean cookiesSecure;
    private boolean cookiesHttpOnly;
    public String JWT_REFRESH_COOKIE_NANE = "refreshToken";
    public Integer ACCESS_TOKEN_VALIDITY_TIME_MS;
    public Integer REFRESH_TOKEN_VALIDITY_TIME_MS;



    public JwtService(@Value("${token.access-token-seconds}") Integer accessTokenSeconds,
                      @Value("${token.refresh-token-seconds}") Integer refreshTokenSeconds,
                      @Value("${token.secret-key}") String secretKey,
                      @Value("${token.cookies-secure}") Boolean cookiesSecure,
                      @Value("${token.cookies-httpOnly}") Boolean cookiesHttpOnly) {
        this.secretKey = secretKey;
        this.ACCESS_TOKEN_VALIDITY_TIME_MS = 1000 * accessTokenSeconds;
        this.REFRESH_TOKEN_VALIDITY_TIME_MS = 1000 * refreshTokenSeconds;
        this.cookiesHttpOnly = cookiesHttpOnly;
        this.cookiesSecure = cookiesSecure;
    }


    public String extractLogin(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractDeviceId(String token) {
        return extractClaim(token, claims -> claims.get("deviceId", String.class));
    }

    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    public LocalDateTime extractLastActiveDate(String token) {
        String stringLastActiveDate = extractClaim(token, claims -> claims.get("lastActiveDate", String.class));
        return LocalDateTime.parse(stringLastActiveDate);
    }

    public String generateAccessToken(UserEntity userEntity) {
        return generateAccessToken(new HashMap<>(), userEntity);
    }

    public String generateAccessToken(Map<String, Object> extractClaims, UserEntity userEntity) {
        return Jwts.builder()
                .subject(userEntity.getLogin())
                .claims(extractClaims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_TIME_MS))
                .signWith(getSignInKey())
                .compact();
    }

    public String generateRefreshToken(UserEntity userEntity) {
        return generateRefreshToken(new HashMap<>(), userEntity);
    }

    public String generateRefreshToken(Map<String, Object> extractClaims, UserEntity userEntity) {
        return Jwts.builder()
                .subject(userEntity.getLogin())
                .claims(extractClaims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY_TIME_MS))
                .signWith(getSignInKey())
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return new SecretKeySpec(keyBytes,"HmacSHA256");
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = this.extractLogin(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenValid(String token, UserEntity userEntity) {
        final String username = this.extractLogin(token);
        return username.equals(userEntity.getLogin()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    // Cookies
    public String getJwtRefreshFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, JWT_REFRESH_COOKIE_NANE);
    }

    private String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    public void setRefreshTokenInCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie(JWT_REFRESH_COOKIE_NANE, refreshToken);
        refreshTokenCookie.setHttpOnly(cookiesHttpOnly);
        refreshTokenCookie.setSecure(cookiesSecure);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(REFRESH_TOKEN_VALIDITY_TIME_MS / 1000); // Convert milliseconds to seconds
        response.addCookie(refreshTokenCookie);
    }

    public Map<String, Object> createClaims(UUID userId, UUID deviceId, LocalDateTime lastActiveDate) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("deviceId", deviceId);
        claims.put("lastActiveDate", lastActiveDate.toString());
        return claims;
    }

    public ResponseCookie getCleanJwtRefreshCookie() {
        ResponseCookie cookie = ResponseCookie.from(JWT_REFRESH_COOKIE_NANE, null).build();
        return cookie;
    }
}
