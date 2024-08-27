package org.development.blogApi.core.blog;

import org.development.blogApi.core.blog.dto.response.ViewBlogDto;
import org.development.blogApi.core.blog.entity.Blog;
import org.development.blogApi.core.blog.repository.BlogQueryRepository;
import org.development.blogApi.core.blog.utils.BlogMapper;
import org.development.blogApi.common.dto.CommonQueryParamsDto;
import org.development.blogApi.core.post.repository.PostQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @GetMapping()
    public List<ViewBlogDto> getAllBlogs(CommonQueryParamsDto query) { // TODO check query params
        return this.blogQueryRepository.findAll().stream().map(blog -> BlogMapper.toView(blog)).collect(Collectors.toList()); // Create method
    }

    @GetMapping("/{id}")
    public ViewBlogDto findBlog(@PathVariable String id) {
        Blog blog = this.blogQueryRepository.findById(UUID.fromString(id)).orElseThrow(() -> new RuntimeException("User not found"));
        return BlogMapper.toView(blog);
    }

//    @GetMapping("/{id}/posts")
//    public ResponseEntity<?> findAllPostsOfBlog(@PathVariable String id, QueryUserDto query, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
//        Blog blog = this.blogService.findById(UUID.fromString(id)).orElseThrow(() -> new RuntimeException("Blog not found"));
//        if (blog == null) {
//            return new ResponseEntity<>("Blog not found", HttpStatus.NOT_FOUND);
//        }
//
//        List<ViewPostDto> posts = this.postQueryRepository.findPostsOfBlog(id, query, userId);
//        return ResponseEntity.ok(posts);
//    }
}
