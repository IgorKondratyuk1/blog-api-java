package org.development.blogApi.e2e.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.development.blogApi.core.blog.dto.request.CreateBlogDto;
import org.development.blogApi.core.blog.dto.response.ViewBlogDto;
import org.development.blogApi.core.post.dto.request.CreatePostOfBlogDto;
import org.development.blogApi.core.post.dto.response.ViewPostDto;
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

    public static ResponseEntity<ViewBlogDto> createBlog(TestRestTemplate restTemplate, String accessToken, CreateBlogDto createBlogDto) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + accessToken);

        ObjectMapper Obj = new ObjectMapper();
        String body = Obj.writeValueAsString(createBlogDto);

        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        return restTemplate
                .exchange(
                        "/api/blogger/blogs",
                        HttpMethod.POST,
                        httpEntity,
                        ViewBlogDto.class
                );
    }

    public static ResponseEntity<ViewPostDto> createPostOfBlog(TestRestTemplate restTemplate, String accessToken, CreatePostOfBlogDto createPostOfBlogDto, String blogId) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + accessToken);

        ObjectMapper Obj = new ObjectMapper();
        String body = Obj.writeValueAsString(createPostOfBlogDto);

        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        return restTemplate
                .exchange(
                        "/api/blogger/blogs/" + blogId + "/posts",
                        HttpMethod.POST,
                        httpEntity,
                        ViewPostDto.class
                );
    }
}
