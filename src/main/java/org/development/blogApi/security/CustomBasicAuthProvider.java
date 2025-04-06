package org.development.blogApi.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@Component
public class CustomBasicAuthProvider {
    @Value("${security.basic.username}")
    private String basicUsername;

    @Value("${security.basic.password}")
    private String basicPassword;

    public AuthenticationProvider basicAuthProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String username = authentication.getName();
                String password = authentication.getCredentials().toString();

                // Hardcoded username and password (can be replaced with environment variables)
                if (basicUsername.equals(username) && basicPassword.equals(password)) {
                    return new UsernamePasswordAuthenticationToken(username, password, AuthorityUtils.createAuthorityList("ROLE_USER"));
                } else {
                    throw new BadCredentialsException("Invalid Credentials");
                }
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
    }
}
