package org.development.blogApi.e2e;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.checkerframework.checker.units.qual.A;
import org.development.blogApi.auth.dto.response.AuthResponseDto;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.core.blog.dto.request.CreateBlogDto;
import org.development.blogApi.core.blog.dto.request.UpdateBlogDto;
import org.development.blogApi.core.blog.dto.response.ViewBlogDto;
import org.development.blogApi.core.blog.repository.BlogRepository;
import org.development.blogApi.core.post.dto.request.CreatePostOfBlogDto;
import org.development.blogApi.core.post.dto.request.UpdatePostOfBlogDto;
import org.development.blogApi.core.post.dto.response.ViewPostDto;
import org.development.blogApi.core.post.repository.PostRepository;
import org.development.blogApi.e2e.helpers.TestHelpers;
import org.development.blogApi.e2e.helpers.TestUserData;
import org.development.blogApi.user.dto.response.ViewUserDto;
import org.development.blogApi.user.entity.RoleEntity;
import org.development.blogApi.user.repository.RoleRepository;
import org.development.blogApi.utils.CookieUtil;
import org.development.blogApi.utils.UuidUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BloggerTests {

    // TODO extract user data to help method
    private static TestUserData testUserData = new TestUserData("gotevi9602@konetas.com", "oneUser", "1111111");

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public static void beforeAll(@Autowired RoleRepository roleRepository) {
        roleRepository.save(new RoleEntity(1, "ADMIN"));
        roleRepository.save(new RoleEntity(2, "USER"));
    }

    @Test
    void contextLoads() {
        System.out.println("contextLoads");
    }

    @Test
    @DisplayName("Create User By SA")
    @Order(1)
    void createUser() {
        ResponseEntity<ViewUserDto> response = TestHelpers.createUserBySa(
                restTemplate,
                testUserData.getUsername(),
                testUserData.getPassword(),
                testUserData.getEmail());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getLogin()).isEqualTo(testUserData.getUsername());
        assertThat(response.getBody().getEmail()).isEqualTo(testUserData.getEmail());

        testUserData.setId(UUID.fromString(response.getBody().getId()));
    }

    @Test
    @DisplayName("User can login after password-recovery")
    @Order(2)
    void successLogin() {
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

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Blogs {
        private static List<ViewBlogDto> createdBlogDtoList = new ArrayList<>();

        @AfterAll
        public static void afterAll(@Autowired BlogRepository blogRepository, @Autowired PostRepository postRepository) {
            postRepository.deleteAll();
            blogRepository.deleteAll();
        }

        @Test
        @DisplayName("POST: should create first blog")
        @Order(1)
        void createBlog() throws JsonProcessingException {
            CreateBlogDto createBlogDto = new CreateBlogDto("Blog", "https://www.youtube.com", "some description");
            ResponseEntity<ViewBlogDto> response = TestHelpers.createBlog(
                    restTemplate,
                    testUserData.getAccessToken(),
                    createBlogDto
            );

            createdBlogDtoList.add(response.getBody());

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getName()).isEqualTo(createBlogDto.getName());
            assertThat(response.getBody().getDescription()).isEqualTo(createBlogDto.getDescription());
            assertThat(response.getBody().getWebsiteUrl()).isEqualTo(createBlogDto.getWebsiteUrl());
        }

        @Test
        @DisplayName("POST: should create second blog")
        @Order(2)
        void createSecondBlog() throws JsonProcessingException {
            CreateBlogDto createBlogDto = new CreateBlogDto("Blog 2", "https://www.youtube2.com", "some description 2");
            ResponseEntity<ViewBlogDto> response = TestHelpers.createBlog(
                    restTemplate,
                    testUserData.getAccessToken(),
                    createBlogDto
            );

            createdBlogDtoList.add(response.getBody());

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getName()).isEqualTo(createBlogDto.getName());
            assertThat(response.getBody().getDescription()).isEqualTo(createBlogDto.getDescription());
            assertThat(response.getBody().getWebsiteUrl()).isEqualTo(createBlogDto.getWebsiteUrl());
        }

        @Test
        @DisplayName("GET: should return created blogs")
        @Order(3)
        void getBlogs() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + testUserData.getAccessToken());

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<PaginationDto<ViewBlogDto>> response = restTemplate
                    .exchange(
                            "/api/blogger/blogs?sortDirection=asc",
                            HttpMethod.GET,
                            httpEntity,
                            new ParameterizedTypeReference<PaginationDto<ViewBlogDto>>() {
                            }
                    );

            assertThat(response.getBody().getTotalCount()).isEqualTo(2);
            assertThat(response.getBody().getItems().size()).isEqualTo(2);

            assertThat(response.getBody().getItems().get(0).getId()).isEqualTo(createdBlogDtoList.get(0).getId());
            assertThat(response.getBody().getItems().get(0).getName()).isEqualTo(createdBlogDtoList.get(0).getName());
            assertThat(response.getBody().getItems().get(0).getWebsiteUrl()).isEqualTo(createdBlogDtoList.get(0).getWebsiteUrl());
            assertThat(response.getBody().getItems().get(0).getDescription()).isEqualTo(createdBlogDtoList.get(0).getDescription());
            assertThat(response.getBody().getItems().get(0).getCreatedAt()).isNotNull();

            assertThat(response.getBody().getItems().get(1).getId()).isEqualTo(createdBlogDtoList.get(1).getId());
            assertThat(response.getBody().getItems().get(1).getName()).isEqualTo(createdBlogDtoList.get(1).getName());
            assertThat(response.getBody().getItems().get(1).getWebsiteUrl()).isEqualTo(createdBlogDtoList.get(1).getWebsiteUrl());
            assertThat(response.getBody().getItems().get(1).getDescription()).isEqualTo(createdBlogDtoList.get(1).getDescription());
            assertThat(response.getBody().getItems().get(1).getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("PUT: blog should be updated by correct data")
        @Order(4)
        void updateBlog() throws JsonProcessingException {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + testUserData.getAccessToken());

            UpdateBlogDto updateBlogDto = new UpdateBlogDto("Test Blog Name", "https://www.google.com", "new some description");

            ObjectMapper Obj = new ObjectMapper();
            String body = Obj.writeValueAsString(updateBlogDto);

            HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
            ResponseEntity<Void> response = restTemplate
                    .exchange(
                            "/api/blogger/blogs/" + createdBlogDtoList.get(0).getId(),
                            HttpMethod.PUT,
                            httpEntity,
                            Void.class
                    );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

            createdBlogDtoList.get(0).setName(updateBlogDto.getName());
            createdBlogDtoList.get(0).setWebsiteUrl(updateBlogDto.getWebsiteUrl());
            createdBlogDtoList.get(0).setDescription(updateBlogDto.getDescription());
        }

        @Test
        @DisplayName("GET: search blog by query")
        @Order(5)
        void getBlogByQuery() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + testUserData.getAccessToken());

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<PaginationDto<ViewBlogDto>> response = restTemplate
                    .exchange(
                            "/api/blogger/blogs?searchNameTerm=Test Blog Name",
                            HttpMethod.GET,
                            httpEntity,
                            new ParameterizedTypeReference<PaginationDto<ViewBlogDto>>() {
                            }
                    );

            assertThat(response.getBody().getTotalCount()).isEqualTo(1);
            assertThat(response.getBody().getItems().size()).isEqualTo(1);
            assertThat(response.getBody().getItems().get(0).getId()).isEqualTo(createdBlogDtoList.get(0).getId());
            assertThat(response.getBody().getItems().get(0).getName()).isEqualTo(createdBlogDtoList.get(0).getName());
            assertThat(response.getBody().getItems().get(0).getWebsiteUrl()).isEqualTo(createdBlogDtoList.get(0).getWebsiteUrl());
            assertThat(response.getBody().getItems().get(0).getDescription()).isEqualTo(createdBlogDtoList.get(0).getDescription());
            assertThat(response.getBody().getItems().get(0).getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("DELETE: blog should be deleted")
        @Order(6)
        void deleteBlog() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + testUserData.getAccessToken());

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<Void> response = restTemplate
                    .exchange(
                            "/api/blogger/blogs/" + createdBlogDtoList.get(0).getId(),
                            HttpMethod.DELETE,
                            httpEntity,
                            Void.class
                    );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("GET: check that blog deleted")
        @Order(7)
        void checkThatBlogDeleted() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + testUserData.getAccessToken());

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<PaginationDto<ViewBlogDto>> response = restTemplate
                    .exchange(
                            "/api/blogger/blogs?sortDirection=asc",
                            HttpMethod.GET,
                            httpEntity,
                            new ParameterizedTypeReference<PaginationDto<ViewBlogDto>>() {
                            }
                    );

            assertThat(response.getBody().getTotalCount()).isEqualTo(1);
            assertThat(response.getBody().getItems().size()).isEqualTo(1);

            assertThat(response.getBody().getItems().get(0).getId()).isEqualTo(createdBlogDtoList.get(1).getId());
            assertThat(response.getBody().getItems().get(0).getName()).isEqualTo(createdBlogDtoList.get(1).getName());
            assertThat(response.getBody().getItems().get(0).getWebsiteUrl()).isEqualTo(createdBlogDtoList.get(1).getWebsiteUrl());
            assertThat(response.getBody().getItems().get(0).getDescription()).isEqualTo(createdBlogDtoList.get(1).getDescription());
            assertThat(response.getBody().getItems().get(0).getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Posts {
        private static List<ViewBlogDto> createdBlogDtoList = new ArrayList<>();
        private static List<ViewPostDto> createdPostDtoList = new ArrayList<>();

        @AfterAll
        public static void afterAll(@Autowired BlogRepository blogRepository, @Autowired PostRepository postRepository) {
            postRepository.deleteAll();
            blogRepository.deleteAll();
        }

        @Test
        @DisplayName("POST: should create blog")
        @Order(1)
        void createBlog() throws JsonProcessingException {
            CreateBlogDto createBlogDto = new CreateBlogDto("Blog", "https://www.youtube.com", "some description");
            ResponseEntity<ViewBlogDto> response = TestHelpers.createBlog(
                    restTemplate,
                    testUserData.getAccessToken(),
                    createBlogDto
            );

            createdBlogDtoList.add(response.getBody());

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getName()).isEqualTo(createBlogDto.getName());
            assertThat(response.getBody().getDescription()).isEqualTo(createBlogDto.getDescription());
            assertThat(response.getBody().getWebsiteUrl()).isEqualTo(createBlogDto.getWebsiteUrl());
        }

        @Test
        @DisplayName("POST: should create second blog")
        @Order(2)
        void createSecondBlog() throws JsonProcessingException {
            CreateBlogDto createBlogDto = new CreateBlogDto("Blog 2", "https://www.youtube2.com", "some description 2");
            ResponseEntity<ViewBlogDto> response = TestHelpers.createBlog(
                    restTemplate,
                    testUserData.getAccessToken(),
                    createBlogDto
            );

            createdBlogDtoList.add(response.getBody());

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getName()).isEqualTo(createBlogDto.getName());
            assertThat(response.getBody().getDescription()).isEqualTo(createBlogDto.getDescription());
            assertThat(response.getBody().getWebsiteUrl()).isEqualTo(createBlogDto.getWebsiteUrl());
        }

        @Test
        @DisplayName("POST: should create first post in first blog")
        @Order(3)
        void createFirstPostOfFirstBlog() throws JsonProcessingException {
            CreatePostOfBlogDto createPostOfBlogDto = new CreatePostOfBlogDto("Post 1 of Blog 1", "some descr", "content");
            ResponseEntity<ViewPostDto> response = TestHelpers.createPostOfBlog(
                    restTemplate,
                    testUserData.getAccessToken(),
                    createPostOfBlogDto,
                    createdBlogDtoList.get(0).getId()
            );

            createdPostDtoList.add(response.getBody());

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getTitle()).isEqualTo(createPostOfBlogDto.getTitle());
            assertThat(response.getBody().getShortDescription()).isEqualTo(createPostOfBlogDto.getShortDescription());
            assertThat(response.getBody().getContent()).isEqualTo(createPostOfBlogDto.getContent());
        }

        @Test
        @DisplayName("POST: should create second post in first blog")
        @Order(4)
        void createSecondPostOfFirstBlog() throws JsonProcessingException {
            CreatePostOfBlogDto createPostOfBlogDto = new CreatePostOfBlogDto("Post 2 of Blog 1", "some descr", "content");
            ResponseEntity<ViewPostDto> response = TestHelpers.createPostOfBlog(
                    restTemplate,
                    testUserData.getAccessToken(),
                    createPostOfBlogDto,
                    createdBlogDtoList.get(0).getId()
            );

            createdPostDtoList.add(response.getBody());

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getTitle()).isEqualTo(createPostOfBlogDto.getTitle());
            assertThat(response.getBody().getShortDescription()).isEqualTo(createPostOfBlogDto.getShortDescription());
            assertThat(response.getBody().getContent()).isEqualTo(createPostOfBlogDto.getContent());
        }

        @Test
        @DisplayName("PUT: post should be updated by correct data")
        @Order(5)
        void updateBlog() throws JsonProcessingException {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + testUserData.getAccessToken());

            UpdatePostOfBlogDto updatePostOfBlogDto = new UpdatePostOfBlogDto("updt Post Name", "updt descr", "updt content");

            ObjectMapper Obj = new ObjectMapper();
            String body = Obj.writeValueAsString(updatePostOfBlogDto);

            HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
            ResponseEntity<Void> response = restTemplate
                    .exchange(
                            "/api/blogger/blogs/" + createdPostDtoList.get(0).getBlogId() + "/posts/" + createdPostDtoList.get(0).getId(),
                            HttpMethod.PUT,
                            httpEntity,
                            Void.class
                    );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

            createdPostDtoList.get(0).setContent(updatePostOfBlogDto.getContent());
            createdPostDtoList.get(0).setTitle(updatePostOfBlogDto.getTitle());
            createdPostDtoList.get(0).setShortDescription(updatePostOfBlogDto.getShortDescription());
        }




    }
}
