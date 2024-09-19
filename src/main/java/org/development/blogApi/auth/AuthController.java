package org.development.blogApi.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.development.blogApi.auth.dto.ExtendedLoginDataDto;
import org.development.blogApi.auth.dto.request.*;
import org.development.blogApi.auth.dto.response.AuthResponseDto;
import org.development.blogApi.auth.dto.response.AuthTokensDto;
import org.development.blogApi.security.CustomUserDetails;
import org.development.blogApi.security.JwtService;
import org.development.blogApi.user.UserService;
import org.development.blogApi.auth.dto.request.RegistrationDto;
import org.development.blogApi.user.entity.UserEntity;
import org.development.blogApi.user.utils.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<?> me(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        System.out.println("customUserDetails: " + customUserDetails);
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        UserEntity user = this.userService.findById(UUID.fromString(customUserDetails.getUserId()));
        return new ResponseEntity<>(UserMapper.toViewMe(user),HttpStatus.OK);
    }

    @PostMapping("/registration")
    public ResponseEntity<Void> register(@Valid @RequestBody RegistrationDto registrationDto) {
        authService.register(registrationDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/registration-confirmation")
    public ResponseEntity<Void> confirmRegistration(@Valid @RequestBody RegistrationConfirmationDto registrationConfirmationDto) {
        this.authService.confirmEmail(registrationConfirmationDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping("/registration-email-resending")
    public ResponseEntity<Void> resendConfirmationCode(@Valid @RequestBody RegistrationEmailResendDto registrationEmailResendDto) {
        this.authService.resendConfirmCode(registrationEmailResendDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto,
                                                 @RequestHeader(value = "User-Agent") String userAgent,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {
        try {
            String ipAddress = request.getRemoteAddr();

            // X-Forwarded-For header for proxies or load balancers
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
    public ResponseEntity<Void> logout(HttpServletResponse response, Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        if (customUserDetails == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Cookie refreshCookie = new Cookie(JwtService.JWT_REFRESH_COOKIE_NANE, "");
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = this.jwtService.getJwtRefreshFromCookies(request);

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Refresh Token is empty!");
        }

        AuthTokensDto authTokensDto = this.authService.refresh(refreshToken);
        AuthResponseDto authResponseDto = new AuthResponseDto(authTokensDto.getAccessToken());
        jwtService.setRefreshTokenInCookie(response, authTokensDto.getRefreshToken());
        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }

    @PostMapping("/new-password")
    public ResponseEntity<?> newPassword(@Valid @RequestBody NewPasswordDto newPasswordDto) {
        this.authService.confirmNewPassword(newPasswordDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/password-recovery")
    public ResponseEntity<?> passwordRecovery(@Valid @RequestBody PasswordRecoveryDto passwordRecoveryDto) {
        this.authService.sendRecoveryCode(passwordRecoveryDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
