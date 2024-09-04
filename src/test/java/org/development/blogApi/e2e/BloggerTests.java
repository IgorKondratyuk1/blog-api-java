package org.development.blogApi.e2e;

import org.development.blogApi.user.dto.response.ViewUserDto;
import org.development.blogApi.user.entity.RoleEntity;
import org.development.blogApi.user.repository.RoleRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class BloggerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public static void beforeAll(@Autowired RoleRepository roleRepository) {
        roleRepository.save(new RoleEntity(1, "ADMIN"));
        roleRepository.save(new RoleEntity(2, "USER"));
    }

    @Test
    void contextLoads() {}


    @Test
    void testRegistration() { // POST: should create new user
        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString("admin:admin".getBytes()));
        headers.set("Content-Type", "application/json");

        String body = "{\"login\":\"test123\",\"password\":\"1111111\",\"email\":\"some.mail1234567@gmail.com\"}";
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<ViewUserDto> response = restTemplate
                .exchange(
                        "/api/sa/users",
                        HttpMethod.POST,
                        httpEntity,
                        ViewUserDto.class
                );

        System.out.println(response.getStatusCode());
        System.out.println(response.getBody().toString());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
