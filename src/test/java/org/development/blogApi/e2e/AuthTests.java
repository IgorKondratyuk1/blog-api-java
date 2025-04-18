package org.development.blogApi.e2e;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.development.blogApi.modules.auth.dto.request.LoginDto;
import org.development.blogApi.modules.auth.dto.response.AuthResponseDto;
import org.development.blogApi.modules.auth.dto.response.ViewMeDto;
import org.development.blogApi.e2e.helpers.TestHelpers;
import org.development.blogApi.e2e.helpers.dto.TestTokensPairData;
import org.development.blogApi.e2e.helpers.dto.TestUserData;
import org.development.blogApi.modules.auth.dto.request.RegistrationDto;
import org.development.blogApi.modules.auth.dto.response.ViewUserDto;
import org.development.blogApi.common.exceptions.dto.APIFieldError;
import org.development.blogApi.common.exceptions.dto.APIValidationErrorResult;
import org.development.blogApi.security.JwtService;
import org.development.blogApi.modules.user.entity.RoleEntity;
import org.development.blogApi.modules.user.repository.RoleRepository;
import org.development.blogApi.utils.CookieUtil;
import org.development.blogApi.utils.UuidUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @RegisterExtension
    private static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"))
            .withPerMethodLifecycle(true);

    @BeforeAll
    public static void beforeAll(@Autowired RoleRepository roleRepository) {
        roleRepository.save(new RoleEntity(1, "ADMIN"));
        roleRepository.save(new RoleEntity(2, "USER"));
    }

    @AfterAll
    public static void afterAll() {
        greenMail.stop();
    }

    @Test
    @DisplayName("Context Loads")
    @Order(1)
    void contextLoads() {
        System.out.println("contextLoads");
    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Register {

        private static TestUserData registerUserData = new TestUserData("some.mail1234567@gmail.com", "someUser", "1111111");

        @Test
        @DisplayName("Register User")
        @Order(1)
        void registerUser() throws MessagingException, IOException {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            String body = "{\"login\":\"" + registerUserData.getUsername() + "\",\"password\":\"" + registerUserData.getPassword() + "\",\"email\":\"" + registerUserData.getEmail() + "\"}";
            HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate
                    .exchange(
                            "/api/auth/registration",
                            HttpMethod.POST,
                            httpEntity,
                            String.class
                    );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

            greenMail.waitForIncomingEmail(1); // Wait for email to be received

            // Retrieve the email
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
            MimeMessage receivedMessage = receivedMessages[0];
//        System.out.println(receivedMessage.getAllRecipients());
//        System.out.println(receivedMessage.getSubject());
//        System.out.println(receivedMessage.getContent());

            assertThat(receivedMessage.getAllRecipients()[0].toString()).isEqualTo(registerUserData.getEmail());
            assertThat(receivedMessage.getSubject()).isEqualTo("Email Confirmation");

            UUID confirmationCode = UuidUtil.extractUuidFromMessage(receivedMessage.getContent().toString());
            registerUserData.setConfirmationCode(confirmationCode);
        }

        @Test
        @DisplayName("User can not login without confirmation")
        @Order(2)
        void rejectLoginWithoutConfirmation() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            String body = "{\"loginOrEmail\":\"" + registerUserData.getUsername() + "\", \"password\":\"" + registerUserData.getPassword() + "\"}";
            HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate
                    .exchange(
                            "/api/auth/login",
                            HttpMethod.POST,
                            httpEntity,
                            String.class
                    );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("Confirm user")
        @Order(3)
        void registerConfirmation() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            String body = "{\"code\":\"" + registerUserData.getConfirmationCode() + "\"}";
            HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Void> response = restTemplate
                    .exchange(
                            "/api/auth/registration-confirmation",
                            HttpMethod.POST,
                            httpEntity,
                            Void.class
                    );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("Should return error if confirm-code doesn't exist")
        @Order(4)
        void shouldReturnErrorIfConfirmationCodeDoesntExist() {
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            String body = "{\"code\":\"" + "invalid code" + "\"}";
            HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

            ResponseEntity<APIValidationErrorResult> response = restTemplate
                    .exchange(
                            "/api/auth/registration-confirmation",
                            HttpMethod.POST,
                            httpEntity,
                            APIValidationErrorResult.class
                    );

            List<APIFieldError> expectedApiFieldErrors = new ArrayList<>();
            expectedApiFieldErrors.add(new APIFieldError("Invalid code or code is already used", "code"));
            APIValidationErrorResult expectedApiValidationErrorResult = new APIValidationErrorResult(expectedApiFieldErrors);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isEqualTo(expectedApiValidationErrorResult);
        }

        @Test
        @DisplayName("User can login after confirmation")
        @Order(5)
        void successLoginWithoutConfirmation() {
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            String body = "{\"loginOrEmail\":\"" + registerUserData.getUsername() + "\", \"password\":\"" + registerUserData.getPassword() + "\"}";
            HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

            ResponseEntity<AuthResponseDto> response = restTemplate
                    .exchange(
                            "/api/auth/login",
                            HttpMethod.POST,
                            httpEntity,
                            AuthResponseDto.class
                    );


            String accessToken = response.getBody().getAccessToken();
            String refreshToken = CookieUtil.getValueByKey(response.getHeaders().get("Set-Cookie"), "refreshToken")
                    .orElseThrow(() -> new RuntimeException("No refresh token for test"));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(accessToken).isNotNull();
            assertThat(refreshToken).isNotNull();

            registerUserData.setAccessToken(accessToken);
            registerUserData.setAccessToken(refreshToken);
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class PasswordRecovery {

        private static TestUserData passwordRecoveryUserData = new TestUserData("gotevi9602@konetas.com", "username2", "1111111");

        @Test
        @DisplayName("Create User By SA")
        @Order(1)
        void create() throws JsonProcessingException {
            RegistrationDto createUserDto = new RegistrationDto(
                    passwordRecoveryUserData.getUsername(),
                    passwordRecoveryUserData.getPassword(),
                    passwordRecoveryUserData.getEmail());

            TestHelpers.createUserBySa(restTemplate, createUserDto);
        }

        @Test
        @DisplayName("User can login after SA creation")
        @Order(2)
        void successLoginAfterSACreation() throws JsonProcessingException {
            LoginDto loginDto = new LoginDto(passwordRecoveryUserData.getUsername(), passwordRecoveryUserData.getPassword());
            TestTokensPairData tokensPairData = TestHelpers.login(restTemplate, loginDto);

            passwordRecoveryUserData.setAccessToken(tokensPairData.getAccessToken());
            passwordRecoveryUserData.setRefreshToken(tokensPairData.getRefreshToken());
        }

        @Test
        @DisplayName("Password Recovery")
        @Order(3)
        void passwordRecovery() throws MessagingException, IOException {
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            String body = "{\"email\":\"" + passwordRecoveryUserData.getEmail() + "\"}";
            HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate
                    .exchange(
                            "/api/auth/password-recovery",
                            HttpMethod.POST,
                            httpEntity,
                            String.class
                    );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

            greenMail.waitForIncomingEmail(1); // Wait for email to be received

            // Retrieve the email
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
            MimeMessage receivedMessage = receivedMessages[0];
            System.out.println(receivedMessage.getAllRecipients());
            System.out.println(receivedMessage.getSubject());
            System.out.println(receivedMessage.getContent());

            assertThat(receivedMessage.getAllRecipients()[0].toString()).isEqualTo(passwordRecoveryUserData.getEmail());
            assertThat(receivedMessage.getSubject()).isEqualTo("Email Password Recovery");

            UUID recoveryCode = UuidUtil.extractUuidFromMessage(receivedMessage.getContent().toString());
            passwordRecoveryUserData.setRecoveryCode(recoveryCode);
        }

        @Test
        @DisplayName("New password")
        @Order(4)
        void newPassword() {
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            // Set new password
            passwordRecoveryUserData.setPassword("22222222");

            String body = "{\"newPassword\":\"" + passwordRecoveryUserData.getPassword() + "\", \"recoveryCode\":\"" + passwordRecoveryUserData.getRecoveryCode() + "\"}";
            HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate
                    .exchange(
                            "/api/auth/new-password",
                            HttpMethod.POST,
                            httpEntity,
                            String.class
                    );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("User can login after password-recovery")
        @Order(5)
        void successLoginAfterPasswordRecovery() throws JsonProcessingException {
            LoginDto loginDto = new LoginDto(passwordRecoveryUserData.getUsername(), passwordRecoveryUserData.getPassword());
            TestTokensPairData tokensPairData = TestHelpers.login(restTemplate, loginDto);

            passwordRecoveryUserData.setAccessToken(tokensPairData.getAccessToken());
            passwordRecoveryUserData.setRefreshToken(tokensPairData.getRefreshToken());
        }
    }


    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Logout {

        private static TestUserData logoutUserData = new TestUserData("gotevi3@konetas.com", "username3", "1111111");

        @Test
        @DisplayName("Create User By SA")
        @Order(1)
        void create() throws JsonProcessingException {
            RegistrationDto createUserDto = new RegistrationDto(
                    logoutUserData.getUsername(),
                    logoutUserData.getPassword(),
                    logoutUserData.getEmail());

            ViewUserDto viewUserDto = TestHelpers.createUserBySa(restTemplate, createUserDto);
            logoutUserData.setId(UUID.fromString(viewUserDto.getId()));
        }

        @Test
        @DisplayName("User can login after SA creation")
        @Order(2)
        void successLoginAfterSACreation() throws JsonProcessingException {
            LoginDto loginDto = new LoginDto(logoutUserData.getUsername(), logoutUserData.getPassword());
            TestTokensPairData tokensPairData = TestHelpers.login(restTemplate, loginDto);

            logoutUserData.setAccessToken(tokensPairData.getAccessToken());
            logoutUserData.setRefreshToken(tokensPairData.getRefreshToken());
        }

        @Test
        @DisplayName("Refresh Token")
        @Order(3)
        void refreshToken() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.add(HttpHeaders.COOKIE, jwtService.JWT_REFRESH_COOKIE_NANE + "=" + logoutUserData.getRefreshToken());

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            ResponseEntity<AuthResponseDto> response = restTemplate
                    .exchange(
                            "/api/auth/refresh-token",
                            HttpMethod.POST,
                            httpEntity,
                            AuthResponseDto.class
                    );

            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());

            String accessToken = response.getBody().getAccessToken();
            String refreshToken = CookieUtil.getValueByKey(response.getHeaders().get("Set-Cookie"), "refreshToken")
                    .orElseThrow(() -> new RuntimeException("No refresh token for test"));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(accessToken).isNotNull();
            assertThat(refreshToken).isNotNull();

            logoutUserData.setRefreshToken(refreshToken);
            logoutUserData.setAccessToken(accessToken);
        }

        @Test
        @DisplayName("Me")
        @Order(4)
        void me() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + logoutUserData.getAccessToken());
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            ResponseEntity<ViewMeDto> response = restTemplate
                    .exchange(
                            "/api/auth/me",
                            HttpMethod.GET,
                            httpEntity,
                            ViewMeDto.class
                    );

            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getLogin()).isEqualTo(logoutUserData.getUsername());
            assertThat(response.getBody().getEmail()).isEqualTo(logoutUserData.getEmail());
            assertThat(response.getBody().getUserId()).isEqualTo(logoutUserData.getId().toString());
        }

        @Test
        @DisplayName("Logout")
        @Order(5)
        void logout() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.add(HttpHeaders.COOKIE, jwtService.JWT_REFRESH_COOKIE_NANE + "=" + logoutUserData.getRefreshToken());
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            ResponseEntity<Void> response = restTemplate
                    .exchange(
                            "/api/auth/logout",
                            HttpMethod.POST,
                            httpEntity,
                            Void.class
                    );

            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());

            String refreshToken = CookieUtil.getValueByKey(response.getHeaders().get("Set-Cookie"), "refreshToken")
                    .orElseThrow(() -> new RuntimeException("No refresh token for test"));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(refreshToken).isBlank();
        }
    }
}
