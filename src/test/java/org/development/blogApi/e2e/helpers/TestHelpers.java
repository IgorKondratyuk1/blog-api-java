package org.development.blogApi.e2e.helpers;

import org.development.blogApi.user.dto.response.ViewUserDto;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class TestHelpers {

    public static ResponseEntity<ViewUserDto> createUserBySa(TestRestTemplate restTemplate, String login, String password, String email) {
        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        String body = "{\"login\":\"" + login + "\",\"password\":\"" + password + "\",\"email\":\"" + email + "\"}";
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

        return restTemplate.exchange(
                "/api/sa/users",
                HttpMethod.POST,
                httpEntity,
                ViewUserDto.class
        );
    }
}
