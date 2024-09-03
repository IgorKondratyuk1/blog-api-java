package org.development.blogApi;

import org.development.blogApi.user.entity.RoleEntity;
import org.development.blogApi.user.repository.RoleRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
class AuthTests {

    @Autowired
    private TestRestTemplate restTemplate;

//    @Autowired
//    private RoleRepository roleRepository;

    @BeforeAll
    public static void beforeAll(@Autowired RoleRepository roleRepository) {
        roleRepository.save(new RoleEntity(1, "ADMIN"));
        roleRepository.save(new RoleEntity(2, "USER"));
    }

//    @BeforeEach
//    public void setUp() {
//        roleRepository.save(new RoleEntity(1, "ADMIN"));
//        roleRepository.save(new RoleEntity(2, "USER"));
//    }

    @Test
    void contextLoads() {}

    @Test
    void testRegistration() {
        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        String body = "{\"login\":\"test123\",\"password\":\"1111111\",\"email\":\"some.mail1234567@gmail.com\"}";
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate
                .exchange(
                        "/api/auth/registration",
                        HttpMethod.POST,
                        httpEntity,
                        String.class
                );

        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}
