package org.development.blogApi.e2e;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import org.development.blogApi.auth.dto.response.AuthResponseDto;
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

@Data
class TestUserData {
    private String email = "some.mail1234567@gmail.com";
    private String username = "someUser";
    private String password = "1111111";
    private UUID confirmationCode;

    private String accessToken;
    private String refreshToken;
}

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthTests {

    private static TestUserData testUserData = new TestUserData();

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

    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Register {

        @Test
        @DisplayName("Register User")
        @Order(1)
        void registerUser() throws MessagingException, IOException {
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            String body = "{\"login\":\"" + testUserData.getUsername() + "\",\"password\":\"" + testUserData.getPassword() + "\",\"email\":\"" + testUserData.getEmail() + "\"}";
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

            assertThat(receivedMessage.getAllRecipients()[0].toString()).isEqualTo(testUserData.getEmail());
            assertThat(receivedMessage.getSubject()).isEqualTo("Email Confirmation");

            UUID confirmationCode = UuidUtil.extractUuidFromMessage(receivedMessage.getContent().toString());
            testUserData.setConfirmationCode(confirmationCode);
        }

        @Test
        @DisplayName("User can not login without confirmation")
        @Order(2)
        void rejectLoginWithoutConfirmation() {
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            String body = "{\"loginOrEmail\":\"" + testUserData.getUsername() + "\", \"password\":\"" + testUserData.getPassword() + "\"}";
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

            String body = "{\"code\":\"" + testUserData.getConfirmationCode() + "\"}";
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

            String body = "{\"loginOrEmail\":\"" + testUserData.getUsername() + "\", \"password\":\"" + testUserData.getPassword() + "\"}";
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

            testUserData.setAccessToken(accessToken);
            testUserData.setAccessToken(refreshToken);
        }
    }
}
