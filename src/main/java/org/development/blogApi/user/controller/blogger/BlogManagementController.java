package org.development.blogApi.user.controller.blogger;

import jakarta.validation.Valid;
import org.development.blogApi.infrastructure.common.dto.CommonQueryParamsDto;
import org.development.blogApi.infrastructure.common.dto.PaginationDto;
import org.development.blogApi.blog.dto.response.ViewBlogDto;
import org.development.blogApi.blog.repository.BlogQueryRepository;
import org.development.blogApi.blog.BlogService;
import org.development.blogApi.blog.dto.request.CreateBlogDto;
import org.development.blogApi.blog.dto.request.UpdateBlogDto;
import org.development.blogApi.blog.entity.Blog;
import org.development.blogApi.blog.utils.BlogMapper;
import org.development.blogApi.comment.dto.response.ViewBloggerCommentDto;
import org.development.blogApi.comment.repository.CommentQueryRepository;
import org.development.blogApi.post.dto.response.ViewPostDto;
import org.development.blogApi.post.repository.PostQueryRepository;
import org.development.blogApi.post.PostService;
import org.development.blogApi.post.dto.request.CreatePostOfBlogDto;
import org.development.blogApi.post.dto.request.UpdatePostOfBlogDto;
import org.development.blogApi.post.entity.Post;
import org.development.blogApi.post.utils.PostMapper;
import org.development.blogApi.infrastructure.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/blogger/blogs")
public class BlogManagementController {
    private final BlogService blogService;
    private final PostService postService;
    private final BlogQueryRepository blogQueryRepository;
    private final PostQueryRepository postQueryRepository;
    private final CommentQueryRepository commentQueryRepository;

    @Autowired
    public BlogManagementController(
            BlogService blogService,
            PostService postService,
            BlogQueryRepository blogQueryRepository,
            PostQueryRepository postQueryRepository,
            CommentQueryRepository commentQueryRepository) {
        this.blogService = blogService;
        this.postService = postService;
        this.blogQueryRepository = blogQueryRepository;
        this.postQueryRepository = postQueryRepository;
        this.commentQueryRepository = commentQueryRepository;
    }

    @GetMapping("")
    public PaginationDto<ViewBlogDto> findUserBlogs(CommonQueryParamsDto commonQueryParamsDto,
                                                    @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return blogQueryRepository.findBlogsByCreatedUserId(customUserDetails.getUserId(), commonQueryParamsDto);
    }

    @PostMapping("")
    public ViewBlogDto createBlog(@RequestBody @Valid CreateBlogDto createBlogDto,
                                  @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Blog blog = blogService.create(UUID.fromString(customUserDetails.getUserId()), createBlogDto);
        return BlogMapper.toView(blog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBlog(@PathVariable String id,
                                        @RequestBody @Valid UpdateBlogDto updateBlogDto,
                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        blogService.update(UUID.fromString(customUserDetails.getUserId()), UUID.fromString(id), updateBlogDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeBlog(@PathVariable String id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        blogService.remove(UUID.fromString(customUserDetails.getUserId()), UUID.fromString(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{blogId}/posts")
    public ResponseEntity<?> createPostOfBlog(@PathVariable String blogId,
                                              @RequestBody @Valid CreatePostOfBlogDto createPostOfBlogDto,
                                              @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Post createdPost = postService.create(UUID.fromString(customUserDetails.getUserId()), UUID.fromString(blogId), createPostOfBlogDto);
        return new ResponseEntity<>(PostMapper.toView(createdPost), HttpStatus.OK);
    }

    @GetMapping("/{blogId}/posts")
    public ResponseEntity<?> findUserPosts(@PathVariable String blogId,
                                           CommonQueryParamsDto commonQueryParamsDto,
                                           @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        PaginationDto<ViewPostDto> postsOfBlogByUserId = postQueryRepository.findPostsOfBlogByUserId(blogId, commonQueryParamsDto, customUserDetails.getUserId());
        return new ResponseEntity<>(postsOfBlogByUserId, HttpStatus.OK);
    }

    @PutMapping("/{blogId}/posts/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable String blogId,
                                        @PathVariable String postId,
                                        @RequestBody @Valid UpdatePostOfBlogDto updatePostOfBlogDto,
                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        postService.updateWithBlogId(customUserDetails.getUserId(), postId, blogId, updatePostOfBlogDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{blogId}/posts/{postId}")
    public ResponseEntity<?> removePost(@PathVariable String blogId,
                                        @PathVariable String postId,
                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        postService.removeWithBlogId(customUserDetails.getUserId(), postId, blogId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/comments")
    public ResponseEntity<?> findCommentsOfUserBlog(CommonQueryParamsDto commonQueryParamsDto,
                                                    @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        PaginationDto<ViewBloggerCommentDto> allCommentsOfUserBlogs = commentQueryRepository.findCommentsOfUserBlogs(UUID.fromString(customUserDetails.getUserId()), commonQueryParamsDto);
        return new ResponseEntity<>(allCommentsOfUserBlogs, HttpStatus.OK);
    }
}
