package org.development.blogApi.e2e;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.development.blogApi.auth.dto.request.LoginDto;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.core.blog.dto.request.CreateBlogDto;
import org.development.blogApi.core.blog.dto.response.ViewBlogDto;
import org.development.blogApi.core.blog.repository.BlogRepository;
import org.development.blogApi.core.comment.dto.request.CreateCommentDto;
import org.development.blogApi.core.comment.dto.response.ViewPublicCommentDto;
import org.development.blogApi.core.comment.repository.CommentRepository;
import org.development.blogApi.core.like.enums.LikeStatus;
import org.development.blogApi.core.post.dto.request.CreatePostOfBlogDto;
import org.development.blogApi.core.post.dto.response.ViewPostDto;
import org.development.blogApi.core.post.repository.PostRepository;
import org.development.blogApi.e2e.helpers.TestHelpers;
import org.development.blogApi.e2e.helpers.dto.TestTokensPairData;
import org.development.blogApi.e2e.helpers.dto.TestUserData;
import org.development.blogApi.auth.dto.request.RegistrationDto;
import org.development.blogApi.auth.dto.response.ViewUserDto;
import org.development.blogApi.user.entity.RoleEntity;
import org.development.blogApi.user.repository.RoleRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PublicApiTests {

    // TODO extract user data to help method
    private static TestUserData firstUserData = new TestUserData("gotevi9602@konetas.com", "firstUser", "1111111");
    private static TestUserData secondUserData = new TestUserData("gotevi9601@konetas.com", "secondUser", "22222222");

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
    @DisplayName("Create first user by SA")
    @Order(1)
    void createFirstUser() throws JsonProcessingException {
        RegistrationDto createUserDto = new RegistrationDto(
                firstUserData.getUsername(),
                firstUserData.getPassword(),
                firstUserData.getEmail());
        ViewUserDto viewUserDto = TestHelpers.createUserBySa(restTemplate, createUserDto);

        firstUserData.setId(UUID.fromString(viewUserDto.getId()));
    }

    @Test
    @DisplayName("Create second user by SA")
    @Order(2)
    void createSecondUser() throws JsonProcessingException {
        RegistrationDto createUserDto = new RegistrationDto(
                secondUserData.getUsername(),
                secondUserData.getPassword(),
                secondUserData.getEmail());
        ViewUserDto viewUserDto = TestHelpers.createUserBySa(restTemplate,createUserDto);

        secondUserData.setId(UUID.fromString(viewUserDto.getId()));
    }

    @Test
    @DisplayName("First user can login")
    @Order(3)
    void successLoginWithFirstUser() throws JsonProcessingException {
        LoginDto loginDto = new LoginDto(firstUserData.getUsername(), firstUserData.getPassword());
        TestTokensPairData tokensPairData = TestHelpers.login(restTemplate, loginDto);

        firstUserData.setAccessToken(tokensPairData.getAccessToken());
        firstUserData.setRefreshToken(tokensPairData.getRefreshToken());
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Comments {
        private static List<ViewBlogDto> createdBlogDtoList = new ArrayList<>();
        private static List<ViewPostDto> createdPostDtoList = new ArrayList<>();
        private static List<ViewPublicCommentDto> createdCommentsDtoList = new ArrayList<>();

        @AfterAll
        public static void afterAll(@Autowired BlogRepository blogRepository,
                                    @Autowired PostRepository postRepository,
                                    @Autowired CommentRepository commentRepository
        ) {
            commentRepository.deleteAll();
            postRepository.deleteAll();
            blogRepository.deleteAll();
        }

        @Test
        @DisplayName("POST: should create first blog")
        @Order(1)
        void createFirstBlog() throws JsonProcessingException {
            CreateBlogDto createBlogDto = new CreateBlogDto("Blog 1", "https://www.youtube.com", "some description 1");
            ResponseEntity<ViewBlogDto> response = TestHelpers.createBlog(
                    restTemplate,
                    firstUserData.getAccessToken(),
                    createBlogDto
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getName()).isEqualTo(createBlogDto.getName());
            assertThat(response.getBody().getDescription()).isEqualTo(createBlogDto.getDescription());
            assertThat(response.getBody().getWebsiteUrl()).isEqualTo(createBlogDto.getWebsiteUrl());

            createdBlogDtoList.add(response.getBody());
        }

        @Test
        @DisplayName("POST: should create second blog")
        @Order(2)
        void createSecondBlog() throws JsonProcessingException {
            CreateBlogDto createBlogDto = new CreateBlogDto("Blog 2", "https://www.google.com", "some description 2");
            ResponseEntity<ViewBlogDto> response = TestHelpers.createBlog(
                    restTemplate,
                    firstUserData.getAccessToken(),
                    createBlogDto
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getName()).isEqualTo(createBlogDto.getName());
            assertThat(response.getBody().getDescription()).isEqualTo(createBlogDto.getDescription());
            assertThat(response.getBody().getWebsiteUrl()).isEqualTo(createBlogDto.getWebsiteUrl());

            createdBlogDtoList.add(response.getBody());
        }

        @Test
        @DisplayName("POST: should create first post in first blog")
        @Order(3)
        void createFirstPostOfFirstBlog() throws JsonProcessingException {
            CreatePostOfBlogDto createPostOfBlogDto = new CreatePostOfBlogDto("Post 1 of Blog 1", "some descr", "content");
            ResponseEntity<ViewPostDto> response = TestHelpers.createPostOfBlog(
                    restTemplate,
                    firstUserData.getAccessToken(),
                    createPostOfBlogDto,
                    createdBlogDtoList.get(0).getId()
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getTitle()).isEqualTo(createPostOfBlogDto.getTitle());
            assertThat(response.getBody().getShortDescription()).isEqualTo(createPostOfBlogDto.getShortDescription());
            assertThat(response.getBody().getContent()).isEqualTo(createPostOfBlogDto.getContent());

            createdPostDtoList.add(response.getBody());
        }

        @Test
        @DisplayName("POST: should create first post in second blog")
        @Order(4)
        void createSecondPostOfFirstBlog() throws JsonProcessingException {
            CreatePostOfBlogDto createPostOfBlogDto = new CreatePostOfBlogDto("Post 1 of Blog 2", "some descr", "content");
            ResponseEntity<ViewPostDto> response = TestHelpers.createPostOfBlog(
                    restTemplate,
                    firstUserData.getAccessToken(),
                    createPostOfBlogDto,
                    createdBlogDtoList.get(1).getId()
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getTitle()).isEqualTo(createPostOfBlogDto.getTitle());
            assertThat(response.getBody().getShortDescription()).isEqualTo(createPostOfBlogDto.getShortDescription());
            assertThat(response.getBody().getContent()).isEqualTo(createPostOfBlogDto.getContent());

            createdPostDtoList.add(response.getBody());
        }

        @Test
        @DisplayName("POST: should create first comment of first post")
        @Order(5)
        void createFirstCommentOfFirstPost() throws JsonProcessingException {
            CreateCommentDto createCommentDto = new CreateCommentDto("some content minimum 20 symbols length 1");
            ResponseEntity<ViewPublicCommentDto> response = TestHelpers.createComment(
                    restTemplate,
                    firstUserData.getAccessToken(),
                    createdPostDtoList.get(0).getId(),
                    createCommentDto
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getContent()).isEqualTo(createCommentDto.getContent());
            assertThat(response.getBody().getLikesInfo().getLikesCount()).isEqualTo(0);
            assertThat(response.getBody().getLikesInfo().getDislikesCount()).isEqualTo(0);
            assertThat(response.getBody().getLikesInfo().getMyStatus()).isEqualTo(LikeStatus.NONE);

            createdCommentsDtoList.add(response.getBody());
        }

        @Test
        @DisplayName("POST: should create first comment of first post")
        @Order(6)
        void createSecondCommentOfFirstPost() throws JsonProcessingException {
            CreateCommentDto createCommentDto = new CreateCommentDto("some content minimum 20 symbols length 2");
            ResponseEntity<ViewPublicCommentDto> response = TestHelpers.createComment(
                    restTemplate,
                    firstUserData.getAccessToken(),
                    createdPostDtoList.get(0).getId(),
                    createCommentDto
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getContent()).isEqualTo(createCommentDto.getContent());
            assertThat(response.getBody().getLikesInfo().getLikesCount()).isEqualTo(0);
            assertThat(response.getBody().getLikesInfo().getDislikesCount()).isEqualTo(0);
            assertThat(response.getBody().getLikesInfo().getMyStatus()).isEqualTo(LikeStatus.NONE);

            createdCommentsDtoList.add(response.getBody());
        }

        @Test
        @DisplayName("POST: should create second comment of first post")
        @Order(7)
        void getCommentsOfFirstPost() {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + firstUserData.getAccessToken());

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<PaginationDto<ViewPublicCommentDto>> response = restTemplate.exchange(
                    "/api/posts/" + createdPostDtoList.get(0).getId() + "/comments?sortDirection=asc",
                    HttpMethod.GET,
                    httpEntity,
                    new ParameterizedTypeReference<PaginationDto<ViewPublicCommentDto>>() {}
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            List<ViewPublicCommentDto> comments = response.getBody().getItems();
            assertThat(comments.get(0).getContent()).isEqualTo(createdCommentsDtoList.get(0).getContent());
            assertThat(comments.get(1).getContent()).isEqualTo(createdCommentsDtoList.get(1).getContent());
        }
    }
}
