package org.development.blogApi.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
    public ResponseEntity<ViewMeDto> me(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
//        if (customUserDetails == null) {
//            throw new AuthException("User not found");
//        }

        UserEntity user = this.userService.findById(UUID.fromString(customUserDetails.getUserId()));
        return new ResponseEntity<>(UserMapper.toViewMe(user),HttpStatus.OK);
    }

    @PostMapping("/registration")
    public ResponseEntity<Void> register(@RequestBody @Valid RegistrationDto registrationDto) {
        authService.register(registrationDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/registration-confirmation")
    public ResponseEntity<Void> confirmRegistration(@RequestBody @Valid RegistrationConfirmationDto registrationConfirmationDto) {
        this.authService.confirmEmail(registrationConfirmationDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/registration-email-resending")
    public ResponseEntity<Void> resendConfirmationCode(@RequestBody @Valid RegistrationEmailResendDto registrationEmailResendDto) {
        this.authService.resendConfirmCode(registrationEmailResendDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Cookie refreshCookie = new Cookie(jwtService.JWT_REFRESH_COOKIE_NANE, ""); // TODO transfer to utils
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        this.authService.logout(customUserDetails.getUserId(), customUserDetails.getDeviceId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refresh(@AuthenticationPrincipal CustomUserDetails customUserDetails, HttpServletResponse response) {
//        String refreshToken = this.jwtService.getJwtRefreshFromCookies(request);
//        if (refreshToken == null || refreshToken.isEmpty()) {
//            return new ResponseEntity("Refresh token is empty", HttpStatus.UNAUTHORIZED);
//        }

        AuthTokensDto authTokensDto = this.authService.generateNewTokensPair(customUserDetails);
        AuthResponseDto authResponseDto = new AuthResponseDto(authTokensDto.getAccessToken());
        jwtService.setRefreshTokenInCookie(response, authTokensDto.getRefreshToken());
        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }

    @PostMapping("/new-password")
    public ResponseEntity<?> newPassword(@RequestBody @Valid NewPasswordDto newPasswordDto) {
        this.authService.confirmNewPassword(newPasswordDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/password-recovery")
    public ResponseEntity<?> passwordRecovery(@RequestBody @Valid PasswordRecoveryDto passwordRecoveryDto) {
        this.authService.sendRecoveryCode(passwordRecoveryDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
