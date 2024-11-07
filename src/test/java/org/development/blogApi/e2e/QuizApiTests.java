package org.development.blogApi.e2e;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.development.blogApi.quiz.question.dto.request.CreateQuestionDto;
import org.development.blogApi.quiz.question.dto.response.ViewQuestionDto;
import org.development.blogApi.user.entity.RoleEntity;
import org.development.blogApi.user.repository.RoleRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuizApiTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public static void beforeAll(@Autowired RoleRepository roleRepository) {
        roleRepository.save(new RoleEntity(1, "ADMIN"));
        roleRepository.save(new RoleEntity(2, "USER"));
    }

    @Test
    @Order(1)
    void createQuestion() throws JsonProcessingException {
        CreateQuestionDto createQuestionDto = new CreateQuestionDto(
                "test1",
                new ArrayList<>(Arrays.asList("a", "b"))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
//        headers.set("Authorization", "Basic YWRtaW46cXdlcnR5");

        ObjectMapper Obj = new ObjectMapper();
        String body = Obj.writeValueAsString(createQuestionDto);
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<ViewQuestionDto> response = restTemplate.exchange(
                "/api/quiz/questions",
                HttpMethod.POST,
                httpEntity,
                ViewQuestionDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getBody()).isEqualTo(createQuestionDto.getBody());
        Assertions.assertArrayEquals(response.getBody().getCorrectAnswers().toArray(), createQuestionDto.getCorrectAnswers().toArray());
    }

    @Test
    @Order(2)
    void findAllQuestions() {
        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Basic YWRtaW46cXdlcnR5");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ViewQuestionDto> response = restTemplate.exchange(
                "/api/quiz/questions",
                HttpMethod.GET,
                httpEntity,
                ViewQuestionDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
