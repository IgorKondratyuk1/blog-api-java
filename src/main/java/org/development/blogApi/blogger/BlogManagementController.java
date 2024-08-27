package org.development.blogApi.blogger;

import org.development.blogApi.common.dto.CommonQueryParamsDto;
import org.development.blogApi.core.blog.repository.BlogQueryRepository;
import org.development.blogApi.core.blog.BlogService;
import org.development.blogApi.core.blog.dto.request.CreateBlogDto;
import org.development.blogApi.core.blog.dto.request.UpdateBlogDto;
import org.development.blogApi.core.blog.entity.Blog;
import org.development.blogApi.core.comment.repository.CommentQueryRepository;
import org.development.blogApi.core.post.repository.PostQueryRepository;
import org.development.blogApi.core.post.PostService;
import org.development.blogApi.core.post.dto.request.CreatePostOfBlogDto;
import org.development.blogApi.core.post.dto.request.UpdatePostOfBlogDto;
import org.development.blogApi.core.post.entity.Post;
import org.development.blogApi.core.post.utils.PostMapper;
import org.development.blogApi.security.CustomUserDetails;
import org.development.blogApi.user.dto.request.QueryUserDto;
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

    // TODO Test
    @GetMapping("/test/users")
    public ResponseEntity<?> testFindUserBlogs(QueryUserDto queryUserDto) {
        System.out.println(queryUserDto);
        return ResponseEntity.ok(this.userQueryRepository.findAllUsersWithCustomQueries(queryUserDto));
    }


    @GetMapping
    public ResponseEntity<?> findUserBlogs(
            CommonQueryParamsDto commonQueryParamsDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        System.out.println("customUserDetails: " + customUserDetails);
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        var blogs = blogQueryRepository.findBlogsByCreatedUserId(customUserDetails.getUserId(), commonQueryParamsDto);
        return new ResponseEntity<>(blogs, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createBlog(
            @RequestBody CreateBlogDto createBlogDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        System.out.println("customUserDetails: " + customUserDetails);
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Blog blog = blogService.create(UUID.fromString(customUserDetails.getUserId()), createBlogDto);
        return new ResponseEntity<>(blog, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> updateBlog(
            @PathVariable String id,
            @RequestBody UpdateBlogDto updateBlogDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        System.out.println("customUserDetails: " + customUserDetails);
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        blogService.update(UUID.fromString(customUserDetails.getUserId()), UUID.fromString(id), updateBlogDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> removeBlog(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        System.out.println("customUserDetails: " + customUserDetails);
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        blogService.remove(UUID.fromString(customUserDetails.getUserId()), UUID.fromString(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{blogId}/posts")
    public ResponseEntity<?> createPostOfBlog(
            @PathVariable String blogId,
            @RequestBody CreatePostOfBlogDto createPostOfBlogDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        System.out.println("customUserDetails: " + customUserDetails);
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

        return new ResponseEntity<>(postQueryRepository.findPostsOfBlogByUserId(blogId, commonQueryParamsDto, customUserDetails.getUserId()), HttpStatus.OK);
    }

    @PutMapping("/{blogId}/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> updatePost(
            @PathVariable String blogId,
            @PathVariable String postId,
            @RequestBody UpdatePostOfBlogDto updatePostOfBlogDto,
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
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

        return new ResponseEntity<>(commentQueryRepository.findAllCommentsOfUserBlogs(UUID.fromString(customUserDetails.getUserId()), commonQueryParamsDto), HttpStatus.OK);
    }
}
