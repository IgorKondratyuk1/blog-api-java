package org.development.blogApi.auth;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.development.blogApi.auth.dto.ExtendedLoginDataDto;
import org.development.blogApi.auth.dto.request.*;
import org.development.blogApi.auth.dto.response.AuthResponseDto;
import org.development.blogApi.auth.dto.response.AuthTokensDto;
import org.development.blogApi.auth.dto.response.ViewMeDto;
import org.development.blogApi.security.CustomUserDetails;
import org.development.blogApi.security.JwtService;
import org.development.blogApi.user.UserService;
import org.development.blogApi.auth.dto.request.RegistrationDto;
import org.development.blogApi.user.entity.UserEntity;
import org.development.blogApi.user.utils.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, JwtService jwtService, UserService userService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @GetMapping("/me")
    public ViewMeDto me(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserEntity user = this.userService.findById(UUID.fromString(customUserDetails.getUserId()));
        return UserMapper.toViewMe(user);
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void register(@RequestBody @Valid RegistrationDto registrationDto) {
        authService.register(registrationDto);
    }

    @PostMapping("/registration-confirmation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirmRegistration(@RequestBody @Valid RegistrationConfirmationDto registrationConfirmationDto) {
        this.authService.confirmEmail(registrationConfirmationDto);
    }

    @PostMapping("/registration-email-resending")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resendConfirmationCode(@RequestBody @Valid RegistrationEmailResendDto registrationEmailResendDto) {
        this.authService.resendConfirmCode(registrationEmailResendDto);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid LoginDto loginDto,
                                                 @RequestHeader(value = "User-Agent") String userAgent,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {
        try {
            String ipAddress = request.getRemoteAddr();
            String xfHeader = request.getHeader("X-Forwarded-For");
            if (xfHeader != null) {
                ipAddress = xfHeader.split(",")[0];
            }

            ExtendedLoginDataDto extendedLoginDataDto = new ExtendedLoginDataDto(loginDto.getLoginOrEmail(),
                    loginDto.getPassword(), ipAddress, userAgent);
            AuthTokensDto authTokensDto = authService.login(extendedLoginDataDto);

            AuthResponseDto authResponseDto = new AuthResponseDto(authTokensDto.getAccessToken());
            jwtService.setRefreshTokenInCookie(response, authTokensDto.getRefreshToken());
            return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletResponse response, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Cookie refreshCookie = new Cookie(jwtService.JWT_REFRESH_COOKIE_NANE, ""); // TODO transfer to utils
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        this.authService.logout(UUID.fromString(customUserDetails.getUserId()), UUID.fromString(customUserDetails.getDeviceId()));
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/refresh-token")
    public AuthResponseDto refresh(@AuthenticationPrincipal CustomUserDetails customUserDetails, HttpServletResponse response) {
        AuthTokensDto authTokensDto = this.authService.generateNewTokensPair(customUserDetails);
        AuthResponseDto authResponseDto = new AuthResponseDto(authTokensDto.getAccessToken());
        jwtService.setRefreshTokenInCookie(response, authTokensDto.getRefreshToken());
        return authResponseDto;
    }

    @PostMapping("/new-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void newPassword(@RequestBody @Valid NewPasswordDto newPasswordDto) {
        this.authService.confirmNewPassword(newPasswordDto);
    }

    @PostMapping("/password-recovery")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void passwordRecovery(@RequestBody @Valid PasswordRecoveryDto passwordRecoveryDto) {
        this.authService.sendRecoveryCode(passwordRecoveryDto);
    }
}
