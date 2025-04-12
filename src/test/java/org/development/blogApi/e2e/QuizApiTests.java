package org.development.blogApi.e2e;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.e2e.helpers.TestHelpers;
import org.development.blogApi.e2e.helpers.dto.TestTokensPairData;
import org.development.blogApi.e2e.helpers.dto.TestUserData;
import org.development.blogApi.modules.auth.dto.request.LoginDto;
import org.development.blogApi.modules.auth.dto.request.RegistrationDto;
import org.development.blogApi.modules.auth.dto.response.ViewUserDto;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.response.ViewGamePairDto;
import org.development.blogApi.modules.quiz.question.dto.request.CreateQuestionDto;
import org.development.blogApi.modules.quiz.question.dto.response.ViewQuestionDto;
import org.development.blogApi.modules.user.entity.RoleEntity;
import org.development.blogApi.modules.user.repository.RoleRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuizApiTests {

    private static TestUserData firstTestUser = new TestUserData("aaaaa@mail.com", "firstUser", "1111111");
    private static TestUserData secondTestUser = new TestUserData("bbbbb@mail.com", "secondUser", "2222222");

    private static ViewGamePairDto connectedGamePair;


    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public static void beforeAll(@Autowired RoleRepository roleRepository) {
        roleRepository.save(new RoleEntity(1, "ADMIN"));
        roleRepository.save(new RoleEntity(2, "USER"));
    }

    @Test
    @DisplayName("Create Users By SA")
    @Order(1)
    void createUsers() throws JsonProcessingException {
        RegistrationDto createFirstUserDto = new RegistrationDto(
                firstTestUser.getUsername(),
                firstTestUser.getPassword(),
                firstTestUser.getEmail());

        ViewUserDto viewFirstUserDto = TestHelpers.createUserBySa(restTemplate, createFirstUserDto);
        firstTestUser.setId(UUID.fromString(viewFirstUserDto.getId()));

        RegistrationDto createSecondUserDto = new RegistrationDto(
                secondTestUser.getUsername(),
                secondTestUser.getPassword(),
                secondTestUser.getEmail());

        ViewUserDto viewSecondUserDto = TestHelpers.createUserBySa(restTemplate, createSecondUserDto);
        secondTestUser.setId(UUID.fromString(viewSecondUserDto.getId()));
    }

    @Test
    @Order(2)
    void createTenQuestions() throws JsonProcessingException {
        for (int i = 0; i < 10; i++) {
            CreateQuestionDto createQuestionDto = new CreateQuestionDto(
                    "question " + i,
                    new ArrayList<>(Arrays.asList("answer 1 " + i, "answer 2 " + i))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Basic YWRtaW46cXdlcnR5");

            ObjectMapper Obj = new ObjectMapper();
            String body = Obj.writeValueAsString(createQuestionDto);
            HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

            ResponseEntity<ViewQuestionDto> response = restTemplate.exchange(
                    "/api/sa/quiz/questions",
                    HttpMethod.POST,
                    httpEntity,
                    ViewQuestionDto.class
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(Objects.requireNonNull(response.getBody()).getBody()).isEqualTo(createQuestionDto.body());
            Assertions.assertArrayEquals(response.getBody().getCorrectAnswers().toArray(), createQuestionDto.correctAnswers().toArray());
        }
    }

    @Test
    @Order(3)
    void findAllQuestions() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic YWRtaW46cXdlcnR5");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<PaginationDto<ViewQuestionDto>> response = restTemplate.exchange(
                "/api/sa/quiz/questions",
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<PaginationDto<ViewQuestionDto>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getTotalCount()).isEqualTo(10);
        assertThat(Objects.requireNonNull(response.getBody()).getItems().size()).isEqualTo(10);
    }

    @Test
    @Order(4)
    void createGamePairByFirstUser() throws JsonProcessingException {
        LoginDto firstUserloginDto = new LoginDto(firstTestUser.getUsername(), firstTestUser.getPassword());
        TestTokensPairData firstUserTokensPairData = TestHelpers.login(restTemplate, firstUserloginDto);

        firstTestUser.setAccessToken(firstUserTokensPairData.getAccessToken());
        firstTestUser.setRefreshToken(firstUserTokensPairData.getRefreshToken());


        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + firstTestUser.getAccessToken());

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<ViewGamePairDto> response = restTemplate
                .exchange(
                        "/api/pair-game-quiz/pairs/connection",
                        HttpMethod.POST,
                        httpEntity,
                        ViewGamePairDto.class
                );

        connectedGamePair = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).id()).isNotEmpty();
    }

    @Test
    @Order(5)
    void connectToGamePairBySecondUser() throws JsonProcessingException {
        LoginDto secondUserloginDto = new LoginDto(secondTestUser.getUsername(), secondTestUser.getPassword());
        TestTokensPairData secondUserTokensPairData = TestHelpers.login(restTemplate, secondUserloginDto);

        secondTestUser.setAccessToken(secondUserTokensPairData.getAccessToken());
        secondTestUser.setRefreshToken(secondUserTokensPairData.getRefreshToken());


        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + secondTestUser.getAccessToken());

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<ViewGamePairDto> response = restTemplate
                .exchange(
                        "/api/pair-game-quiz/pairs/connection",
                        HttpMethod.POST,
                        httpEntity,
                        ViewGamePairDto.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).id()).isNotEmpty();
        assertThat(Objects.requireNonNull(response.getBody()).id()).isEqualTo(connectedGamePair.id());
    }

    @Test
    @Order(6)
    void tryToDelete() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic YWRtaW46cXdlcnR5");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/testing/all-data",
                HttpMethod.DELETE,
                httpEntity,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
