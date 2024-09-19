package org.development.blogApi.blogger;

import jakarta.validation.Valid;
import org.development.blogApi.common.dto.CommonQueryParamsDto;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.core.blog.dto.response.ViewBlogDto;
import org.development.blogApi.core.blog.repository.BlogQueryRepository;
import org.development.blogApi.core.blog.BlogService;
import org.development.blogApi.core.blog.dto.request.CreateBlogDto;
import org.development.blogApi.core.blog.dto.request.UpdateBlogDto;
import org.development.blogApi.core.blog.entity.Blog;
import org.development.blogApi.core.blog.utils.BlogMapper;
import org.development.blogApi.core.comment.dto.response.ViewBloggerCommentDto;
import org.development.blogApi.core.comment.repository.CommentQueryRepository;
import org.development.blogApi.core.post.dto.response.ViewPostDto;
import org.development.blogApi.core.post.repository.PostQueryRepository;
import org.development.blogApi.core.post.PostService;
import org.development.blogApi.core.post.dto.request.CreatePostOfBlogDto;
import org.development.blogApi.core.post.dto.request.UpdatePostOfBlogDto;
import org.development.blogApi.core.post.entity.Post;
import org.development.blogApi.core.post.utils.PostMapper;
import org.development.blogApi.security.CustomUserDetails;
import org.development.blogApi.user.repository.UserQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/blogger/blogs")
public class BlogManagementController {

    private final UserQueryRepository userQueryRepository;
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
            CommentQueryRepository commentQueryRepository,
            UserQueryRepository userQueryRepository
    ){
        this.userQueryRepository = userQueryRepository;
        this.blogService = blogService;
        this.postService = postService;
        this.blogQueryRepository = blogQueryRepository;
        this.postQueryRepository = postQueryRepository;
        this.commentQueryRepository = commentQueryRepository;
    }

    @GetMapping
    public ResponseEntity<?> findUserBlogs(
            CommonQueryParamsDto commonQueryParamsDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        PaginationDto<ViewBlogDto> blogs = blogQueryRepository.findBlogsByCreatedUserId(customUserDetails.getUserId(), commonQueryParamsDto);
        return new ResponseEntity<>(blogs, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createBlog(
            @RequestBody @Valid CreateBlogDto createBlogDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Blog blog = blogService.create(UUID.fromString(customUserDetails.getUserId()), createBlogDto);
        return new ResponseEntity<>(BlogMapper.toView(blog), HttpStatus.OK);
    }

    // TODO Make annotation that will be check AuthenticationPrincipal
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBlog(
            @PathVariable String id,
            @RequestBody @Valid UpdateBlogDto updateBlogDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        blogService.update(UUID.fromString(customUserDetails.getUserId()), UUID.fromString(id), updateBlogDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeBlog(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        blogService.remove(UUID.fromString(customUserDetails.getUserId()), UUID.fromString(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{blogId}/posts")
    public ResponseEntity<?> createPostOfBlog(
            @PathVariable String blogId,
            @RequestBody @Valid CreatePostOfBlogDto createPostOfBlogDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Post createdPost = postService.create(UUID.fromString(customUserDetails.getUserId()), UUID.fromString(blogId), createPostOfBlogDto);
        return new ResponseEntity<>(PostMapper.toView(createdPost), HttpStatus.OK);
    }

    @GetMapping("/{blogId}/posts")
    public ResponseEntity<?> findUserPosts(
            @PathVariable String blogId,
            CommonQueryParamsDto commonQueryParamsDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        System.out.println("customUserDetails: " + customUserDetails);
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        PaginationDto<ViewPostDto> postsOfBlogByUserId = postQueryRepository.findPostsOfBlogByUserId(blogId, commonQueryParamsDto, customUserDetails.getUserId());
        return new ResponseEntity<>(postsOfBlogByUserId, HttpStatus.OK);
    }

    @PutMapping("/{blogId}/posts/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable String blogId,
            @PathVariable String postId,
            @RequestBody @Valid UpdatePostOfBlogDto updatePostOfBlogDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        System.out.println("customUserDetails: " + customUserDetails);
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        postService.updateWithBlogId(customUserDetails.getUserId(), postId, blogId, updatePostOfBlogDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{blogId}/posts/{postId}")
    public ResponseEntity<?> removePost(
            @PathVariable String blogId,
            @PathVariable String postId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        System.out.println("customUserDetails: " + customUserDetails);
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        postService.removeWithBlogId(customUserDetails.getUserId(), postId, blogId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/comments")
    public ResponseEntity<?> findCommentsOfUserBlog(
            CommonQueryParamsDto commonQueryParamsDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        System.out.println("customUserDetails: " + customUserDetails);
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        PaginationDto<ViewBloggerCommentDto> allCommentsOfUserBlogs = commentQueryRepository.findCommentsOfUserBlogs(UUID.fromString(customUserDetails.getUserId()), commonQueryParamsDto);
        return new ResponseEntity<>(allCommentsOfUserBlogs, HttpStatus.OK);
    }
}
