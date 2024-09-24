package org.development.blogApi.core.blog;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.development.blogApi.common.dto.CommonQueryParamsDto;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.core.blog.dto.response.ViewBlogDto;
import org.development.blogApi.core.blog.entity.Blog;
import org.development.blogApi.core.blog.repository.BlogQueryRepository;
import org.development.blogApi.core.post.dto.response.ViewPostDto;
import org.development.blogApi.core.post.repository.PostQueryRepository;
import org.development.blogApi.security.CustomUserDetails;
import org.development.blogApi.auth.dto.request.QueryUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/blogs")
public class BlogController {
    private final BlogQueryRepository blogQueryRepository;
    private final BlogService blogService;

    private final PostQueryRepository postQueryRepository;

    @Autowired
    public BlogController(BlogQueryRepository blogQueryRepository, PostQueryRepository postQueryRepository, BlogService blogService) {
        this.blogQueryRepository = blogQueryRepository;
        this.postQueryRepository = postQueryRepository;
        this.blogService = blogService;
    }

    @RateLimiter(name = "rateLimiterApi")
    @GetMapping()
    public ResponseEntity<PaginationDto<ViewBlogDto>> findAllBlogs(CommonQueryParamsDto query) { // TODO check query params
        PaginationDto<ViewBlogDto> paginationDto = this.blogQueryRepository.findAllBlogs(query, false);
        return new ResponseEntity<>(paginationDto, HttpStatus.OK);
    }

    @RateLimiter(name = "rateLimiterApi")
    @GetMapping("/{id}")
    public ResponseEntity<?> findBlogById(@PathVariable String id) {
        Optional<ViewBlogDto> optionalViewBlogDto = this.blogQueryRepository.findOneBlog(id);

        if (optionalViewBlogDto.isEmpty()) {
            return new ResponseEntity<>("Blog not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalViewBlogDto.get(), HttpStatus.OK);
    }

    @RateLimiter(name = "rateLimiterApi")
    @GetMapping("/{id}/posts")
    public ResponseEntity<?> findAllPostsOfBlog(@PathVariable String id, QueryUserDto query,
                                                @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Blog blog = this.blogService.findById(UUID.fromString(id)).orElseThrow(() -> new RuntimeException("Blog not found"));
        if (blog == null) {
            return new ResponseEntity<>("Blog not found", HttpStatus.NOT_FOUND);
        }

        PaginationDto<ViewPostDto> paginationDto = this.postQueryRepository.findPostsOfBlog(id, query, customUserDetails.getUserId());
        return ResponseEntity.ok(paginationDto);
    }
}
