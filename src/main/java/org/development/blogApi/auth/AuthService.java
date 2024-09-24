package org.development.blogApi.auth;

import org.development.blogApi.auth.dto.*;
import org.development.blogApi.auth.dto.request.NewPasswordDto;
import org.development.blogApi.auth.dto.request.PasswordRecoveryDto;
import org.development.blogApi.auth.dto.request.RegistrationConfirmationDto;
import org.development.blogApi.auth.dto.request.RegistrationEmailResendDto;
import org.development.blogApi.auth.dto.response.AuthTokensDto;
import org.development.blogApi.email.EmailManager;
import org.development.blogApi.exceptions.authExceptions.AuthException;
import org.development.blogApi.exceptions.userExceptions.UserNotFoundException;
import org.development.blogApi.security.CustomUserDetails;
import org.development.blogApi.security.JwtService;
import org.development.blogApi.securityDevice.SecurityDeviceService;
import org.development.blogApi.securityDevice.dto.CreateSecurityDeviceDto;
import org.development.blogApi.securityDevice.entity.SecurityDevice;
import org.development.blogApi.user.repository.RoleRepository;
import org.development.blogApi.user.repository.UserRepository;
import org.development.blogApi.auth.dto.request.RegistrationDto;
import org.development.blogApi.user.entity.RoleEntity;
import org.development.blogApi.user.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;


@Service
public class AuthService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private EmailManager emailManager;

    private SecurityDeviceService securityDeviceService;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                          JwtService jwtService, EmailManager emailManager, SecurityDeviceService securityDeviceService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailManager = emailManager;
        this.securityDeviceService = securityDeviceService;
    }

    public void register(RegistrationDto createUserDto) {
        if (userRepository.existsByLogin(createUserDto.getLogin())) throw new RuntimeException("User is taken");

        // 1. Create new user
        RoleEntity roles = roleRepository.findRoleEntityByName("USER").orElseThrow(() -> new RuntimeException("Role not found"));
        UserEntity user = UserEntity.createInstance(createUserDto, passwordEncoder.encode(createUserDto.getPassword()), Collections.singletonList(roles));
        userRepository.save(user);

        // 2. Try to send password confirmation email
        this.emailManager.sendEmailConfirmationMessage(user.getEmail(), user.getEmailConfirmation().getConfirmationCode().toString());
    }

    public void confirmEmail(RegistrationConfirmationDto registrationConfirmationDto) {
        UUID code = UUID.fromString(registrationConfirmationDto.getCode());
        UserEntity user = this.userRepository.findByEmailConfirmationCode(code).orElseThrow(() -> new RuntimeException("User not found by ConfirmationCode"));
        user.confirm(code);
        this.userRepository.save(user);
    }

    public void resendConfirmCode(RegistrationEmailResendDto registrationEmailResendDto) {
        // 1. Check user
        UserEntity user = this.userRepository.findByLoginOrEmail(registrationEmailResendDto.getEmail()).orElseThrow(() -> new UserNotFoundException());
        if (user.getEmailConfirmation().isConfirmed()) { throw new RuntimeException("User is already confirmed"); }

        // 2. Renew and save ConfirmationCode
        user.getEmailConfirmation().setConfirmationCode(UUID.randomUUID());
        this.userRepository.save(user);

        // 3. Resend code via mail
        this.emailManager.sendEmailConfirmationMessage(user.getEmail(), user.getEmailConfirmation().getConfirmationCode().toString());
    }

    public AuthTokensDto login(ExtendedLoginDataDto extendedLoginDataDto) {
        UserEntity userEntity = this.userRepository.findByLoginOrEmail(extendedLoginDataDto.getLoginOrEmail()).orElseThrow(() -> new UserNotFoundException());
        if (!userEntity.getEmailConfirmation().isConfirmed()) { throw new RuntimeException("User not confirmed"); }

        CreateSecurityDeviceDto createSecurityDeviceDto = new CreateSecurityDeviceDto(
                userEntity.getId().toString(),
                extendedLoginDataDto.getIp(),
                extendedLoginDataDto.getTitle());
        SecurityDevice securityDevice = this.securityDeviceService.createDeviceSession(createSecurityDeviceDto);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        extendedLoginDataDto.getLoginOrEmail(),
                        extendedLoginDataDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Map<String, Object> claims = jwtService.createClaims(userEntity.getId(), securityDevice.getDeviceId(), securityDevice.getLastActiveDate());
        String accessToken = jwtService.generateAccessToken(claims, userEntity);
        String refreshToken = jwtService.generateRefreshToken(claims, userEntity);
        return new AuthTokensDto(accessToken, refreshToken);
    }

    public AuthTokensDto generateNewTokensPair(CustomUserDetails customUserDetails) {
        if (customUserDetails.getUsername() == null || customUserDetails.getDeviceId() == null) {
            throw new AuthException("User details not valid");
        }

        UserEntity userEntity = this.userRepository.findByLoginOrEmail(customUserDetails.getUsername()).orElseThrow(() -> new UserNotFoundException());
        SecurityDevice securityDevice = this.securityDeviceService.findDeviceSessionByDeviceId(customUserDetails.getDeviceId());

        Map<String, Object> claims = jwtService.createClaims(userEntity.getId(), securityDevice.getDeviceId(), securityDevice.getLastActiveDate());
        String newAccessToken = jwtService.generateAccessToken(claims, userEntity);
        String newRefreshToken = jwtService.generateRefreshToken(claims, userEntity);
        return new AuthTokensDto(newAccessToken, newRefreshToken);
    }

    public void confirmNewPassword(NewPasswordDto newPasswordDto) {
        UUID code = UUID.fromString(newPasswordDto.getRecoveryCode());
        UserEntity userEntity = this.userRepository.findUserByPasswordConfirmationCode(code).orElseThrow(() -> new RuntimeException("RecoveryCode not found"));

        userEntity.setPassword(passwordEncoder.encode(newPasswordDto.getNewPassword()));
        this.userRepository.save(userEntity);
    }

    public void sendRecoveryCode(PasswordRecoveryDto passwordRecoveryDto) {
        UserEntity userEntity = this.userRepository.findByLoginOrEmail(passwordRecoveryDto.getEmail()).orElseThrow(() -> new RuntimeException("User with email " + passwordRecoveryDto.getEmail() + " not found"));
        userEntity.createNewPasswordRecoveryCode();
        this.userRepository.save(userEntity);

        this.emailManager.sendPasswordRecoveryMessage(userEntity.getEmail(), userEntity.getPasswordRecovery().getRecoveryCode().toString());
    }
}
