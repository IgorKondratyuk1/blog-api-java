package org.development.blogApi.blogPlatform.core.blog;

import org.development.blogApi.auth.dto.request.QueryUserDto;
import org.development.blogApi.blogPlatform.core.blog.dto.response.ViewBlogDto;
import org.development.blogApi.blogPlatform.core.blog.exceptions.BlogNotFoundException;
import org.development.blogApi.blogPlatform.core.blog.repository.BlogQueryRepository;
import org.development.blogApi.infrastructure.common.dto.CommonQueryParamsDto;
import org.development.blogApi.infrastructure.common.dto.PaginationDto;
import org.development.blogApi.blogPlatform.core.post.dto.response.ViewPostDto;
import org.development.blogApi.blogPlatform.core.post.repository.PostQueryRepository;
import org.development.blogApi.infrastructure.security.CustomUserDetails;
import org.development.blogApi.infrastructure.security.annotation.GetUserFromJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/blogs")
public class BlogController {
    private final BlogQueryRepository blogQueryRepository;
    private final BlogService blogService;
    private final PostQueryRepository postQueryRepository;

    @Autowired
    public BlogController(BlogQueryRepository blogQueryRepository,
                          PostQueryRepository postQueryRepository,
                          BlogService blogService)
    {
        this.blogQueryRepository = blogQueryRepository;
        this.postQueryRepository = postQueryRepository;
        this.blogService = blogService;
    }

    @GetMapping("")
    public PaginationDto<ViewBlogDto> findAllBlogs(CommonQueryParamsDto query) { // TODO check query params
        return this.blogQueryRepository.findAllBlogs(query, false);
    }

    @GetMapping("/{id}")
    public ViewBlogDto findBlogById(@PathVariable UUID id) {
        return this.blogQueryRepository.findOneBlog(id).orElseThrow(() -> new BlogNotFoundException());
    }

    @GetUserFromJwt
    @GetMapping("/{id}/posts")
    public PaginationDto<ViewPostDto> findAllPostsOfBlog(@PathVariable String id, QueryUserDto query) {
        // TODO refactor getting user info from SecurityContextHolder
        String userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getName() != "anonymousUser") {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            userId = customUserDetails.getUserId();
        }

        this.blogService.findById(UUID.fromString(id)).orElseThrow(() -> new BlogNotFoundException());
        return this.postQueryRepository.findPostsOfBlog(id, query, userId);
    }
}
