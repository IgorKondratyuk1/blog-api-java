package org.development.blogApi.superAdmin;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import org.development.blogApi.common.dto.CommonQueryParamsDto;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.core.blog.BlogService;
import org.development.blogApi.core.blog.dto.request.CreateBlogDto;
import org.development.blogApi.core.blog.dto.request.UpdateBlogDto;
import org.development.blogApi.core.blog.dto.response.ViewBlogDto;
import org.development.blogApi.core.blog.entity.Blog;
import org.development.blogApi.core.blog.repository.BlogQueryRepository;
import org.development.blogApi.core.blog.utils.BlogMapper;
import org.development.blogApi.core.post.PostService;
import org.development.blogApi.core.post.dto.request.CreatePostOfBlogDto;
import org.development.blogApi.core.post.dto.request.UpdatePostOfBlogDto;
import org.development.blogApi.core.post.dto.response.ViewPostDto;
import org.development.blogApi.core.post.repository.PostQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sa/blogs")
public class SuperAdminBlogsController { // TODO write tests

    private final BlogService blogService;
    private final PostService postService;
    private final BlogQueryRepository blogQueryRepository;
    private final PostQueryRepository postQueryRepository;

    @Autowired
    public SuperAdminBlogsController(BlogService blogService,
                                     PostService postService,
                                     BlogQueryRepository blogQueryRepository,
                                     PostQueryRepository postQueryRepository) {
        this.blogService = blogService;
        this.postService = postService;
        this.blogQueryRepository = blogQueryRepository;
        this.postQueryRepository = postQueryRepository;
    }

    @GetMapping
    public ResponseEntity<PaginationDto<ViewBlogDto>> findAll(CommonQueryParamsDto query) {
        PaginationDto<ViewBlogDto> result = blogQueryRepository.findAllBlogs(query, false);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ViewBlogDto> createBlog(@RequestBody @Valid CreateBlogDto createBlogDto) {
        Blog blog = blogService.createByAdmin(createBlogDto);
        return new ResponseEntity(BlogMapper.toView(blog), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBlog(@PathVariable("id") UUID id,
                                           @RequestBody @Valid UpdateBlogDto updateBlogDto) {
        blogService.updateByAdmin(id, updateBlogDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> removeBlog(@PathVariable("id") UUID id) {
        blogService.removeByAdmin(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{blogId}/posts")
    public ResponseEntity<ViewPostDto> createPostOfBlog(@PathVariable("blogId") UUID blogId,
                                                        @RequestBody @Valid CreatePostOfBlogDto createPostOfBlogDto) {
        ViewPostDto viewPostDto = postService.createByAdmin(blogId, createPostOfBlogDto);
        return new ResponseEntity<>(viewPostDto, HttpStatus.CREATED);
    }

    @GetMapping("/{blogId}/posts")
    public ResponseEntity<PaginationDto<ViewPostDto>> findUserPosts(@PathVariable("blogId") String blogId,
                                                                    CommonQueryParamsDto query) {
        PaginationDto<ViewPostDto> viewPostDto = postQueryRepository.findPostsOfBlog(blogId, query, null);
        return new ResponseEntity<>(viewPostDto, HttpStatus.OK);
    }

    // TODO: Change to UUID (@PathVariable("blogId") String blogId)
    @PutMapping("/{blogId}/posts/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable("blogId") String blogId,
                                           @PathVariable("postId") String postId,
                                           @RequestBody @Valid UpdatePostOfBlogDto updatePostOfBlogDto) {
        postService.updateWithBlogIdByAdmin(postId, blogId, updatePostOfBlogDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{blogId}/posts/{postId}")
    public ResponseEntity<Void> removePost(@PathVariable("blogId") String blogId,
                                           @PathVariable("postId") String postId) {
        postService.removeWithBlogIdByAdmin(postId, blogId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{blogId}/bind-with-user/{userId}")
    public ResponseEntity<Void> bindWithUser(@PathVariable("blogId") UUID blogId,
                                             @PathVariable("userId") UUID userId) {
        blogService.bindBlogWithUser(userId, blogId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

//    @PutMapping("/{blogId}/ban")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public ResponseEntity<Void> banBlog(@PathVariable("blogId") String blogId, @RequestBody BanBlogDto banBlogDto) {
//        Object result = commandBus.execute(new BanBlogCommand(blogId, banBlogDto));
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
}
