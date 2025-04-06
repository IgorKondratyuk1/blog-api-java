package org.development.blogApi.e2e.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.development.blogApi.modules.auth.dto.request.LoginDto;
import org.development.blogApi.modules.auth.dto.response.AuthResponseDto;
import org.development.blogApi.modules.blogPlatform.core.blog.dto.request.CreateBlogDto;
import org.development.blogApi.modules.blogPlatform.core.blog.dto.response.ViewBlogDto;
import org.development.blogApi.modules.blogPlatform.core.comment.dto.request.CreateCommentDto;
import org.development.blogApi.modules.blogPlatform.core.comment.dto.response.ViewPublicCommentDto;
import org.development.blogApi.modules.blogPlatform.core.post.dto.request.CreatePostOfBlogDto;
import org.development.blogApi.modules.blogPlatform.core.post.dto.response.ViewPostDto;
import org.development.blogApi.e2e.helpers.dto.TestTokensPairData;
import org.development.blogApi.modules.auth.dto.request.RegistrationDto;
import org.development.blogApi.modules.auth.dto.response.ViewUserDto;
import org.development.blogApi.utils.CookieUtil;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class TestHelpers {

    public static ViewUserDto createUserBySa(TestRestTemplate restTemplate, RegistrationDto createUserDto) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Basic YWRtaW46cXdlcnR5");

        ObjectMapper Obj = new ObjectMapper();
        String body = Obj.writeValueAsString(createUserDto);
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<ViewUserDto> response = restTemplate.exchange(
                "/api/sa/users",
                HttpMethod.POST,
                httpEntity,
                ViewUserDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getLogin()).isEqualTo(createUserDto.getLogin());
        assertThat(response.getBody().getEmail()).isEqualTo(createUserDto.getEmail());

        return response.getBody();
    }

    public static TestTokensPairData login(TestRestTemplate restTemplate, LoginDto loginDto) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        ObjectMapper Obj = new ObjectMapper();
        String body = Obj.writeValueAsString(loginDto);
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

        return new TestTokensPairData(accessToken, refreshToken);
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

    public static ResponseEntity<ViewPublicCommentDto> createComment(TestRestTemplate restTemplate, String accessToken, String postId, CreateCommentDto createCommentDto) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + accessToken);

        ObjectMapper Obj = new ObjectMapper();
        String body = Obj.writeValueAsString(createCommentDto);

        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        return restTemplate
                .exchange(
                        "/api/posts/" + postId + "/comments",
                        HttpMethod.POST,
                        httpEntity,
                        ViewPublicCommentDto.class
                );
    }
}
