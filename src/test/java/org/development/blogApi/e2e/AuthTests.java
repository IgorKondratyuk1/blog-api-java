package org.development.blogApi.e2e;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import org.development.blogApi.auth.dto.response.AuthResponseDto;
import org.development.blogApi.e2e.helpers.TestHelpers;
import org.development.blogApi.e2e.helpers.TestUserData;
import org.development.blogApi.user.dto.response.ViewUserDto;
import org.development.blogApi.user.entity.RoleEntity;
import org.development.blogApi.user.repository.RoleRepository;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;



@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthTests {

    @Autowired
    private TestRestTemplate restTemplate;

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
            // Set up headers
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
            // Set up headers
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

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Confirm User")
        @Order(3)
        void registerConfirmation() {
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            String body = "{\"code\":\"" + registerUserData.getConfirmationCode() + "\"}";
            HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate
                    .exchange(
                            "/api/auth/registration-confirmation",
                            HttpMethod.POST,
                            httpEntity,
                            String.class
                    );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("User can login after confirmation")
        @Order(4)
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

        private static TestUserData passwordRecoveryUserData = new TestUserData("gotevi9602@konetas.com", "anotherUser", "1111111");

        @Test
        @DisplayName("Create User By SA")
        @Order(1)
        void create() {
            ResponseEntity<ViewUserDto> response = TestHelpers.createUserBySa(
                    restTemplate,
                    passwordRecoveryUserData.getUsername(),
                    passwordRecoveryUserData.getPassword(),
                    passwordRecoveryUserData.getEmail());

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getLogin()).isEqualTo(passwordRecoveryUserData.getUsername());
            assertThat(response.getBody().getEmail()).isEqualTo(passwordRecoveryUserData.getEmail());
        }

        @Test
        @DisplayName("User can login after SA creation")
        @Order(2)
        void successLoginAfterSACreation() {
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            String body = "{\"loginOrEmail\":\"" + passwordRecoveryUserData.getUsername() + "\", \"password\":\"" + passwordRecoveryUserData.getPassword() + "\"}";
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

            passwordRecoveryUserData.setAccessToken(accessToken);
            passwordRecoveryUserData.setAccessToken(refreshToken);
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
        void successLoginAfterPasswordRecovery() {
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            String body = "{\"loginOrEmail\":\"" + passwordRecoveryUserData.getUsername() + "\", \"password\":\"" + passwordRecoveryUserData.getPassword() + "\"}";
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

            passwordRecoveryUserData.setAccessToken(accessToken);
            passwordRecoveryUserData.setAccessToken(refreshToken);
        }
    }
}
