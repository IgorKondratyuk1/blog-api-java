package org.development.blogApi.modules.blogPlatform.blogger;

import jakarta.validation.Valid;
import org.development.blogApi.common.dto.CommonQueryParamsDto;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.modules.blogPlatform.core.blog.dto.response.ViewBlogDto;
import org.development.blogApi.modules.blogPlatform.core.blog.repository.BlogQueryRepository;
import org.development.blogApi.modules.blogPlatform.core.blog.BlogService;
import org.development.blogApi.modules.blogPlatform.core.blog.dto.request.CreateBlogDto;
import org.development.blogApi.modules.blogPlatform.core.blog.dto.request.UpdateBlogDto;
import org.development.blogApi.modules.blogPlatform.core.blog.entity.Blog;
import org.development.blogApi.modules.blogPlatform.core.blog.utils.BlogMapper;
import org.development.blogApi.modules.blogPlatform.core.comment.dto.response.ViewBloggerCommentDto;
import org.development.blogApi.modules.blogPlatform.core.comment.repository.CommentQueryRepository;
import org.development.blogApi.modules.blogPlatform.core.post.dto.response.ViewPostDto;
import org.development.blogApi.modules.blogPlatform.core.post.repository.PostQueryRepository;
import org.development.blogApi.modules.blogPlatform.core.post.PostService;
import org.development.blogApi.modules.blogPlatform.core.post.dto.request.CreatePostOfBlogDto;
import org.development.blogApi.modules.blogPlatform.core.post.dto.request.UpdatePostOfBlogDto;
import org.development.blogApi.modules.blogPlatform.core.post.entity.Post;
import org.development.blogApi.modules.blogPlatform.core.post.utils.PostMapper;
import org.development.blogApi.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBlog(@PathVariable String id,
                                        @RequestBody @Valid UpdateBlogDto updateBlogDto,
                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        blogService.update(UUID.fromString(customUserDetails.getUserId()), UUID.fromString(id), updateBlogDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBlog(@PathVariable String id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        blogService.remove(UUID.fromString(customUserDetails.getUserId()), UUID.fromString(id));
    }

    @PostMapping("/{blogId}/posts")
    @ResponseStatus(HttpStatus.OK)
    public ViewPostDto createPostOfBlog(@PathVariable String blogId,
                                              @RequestBody @Valid CreatePostOfBlogDto createPostOfBlogDto,
                                              @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Post createdPost = postService.create(UUID.fromString(customUserDetails.getUserId()), UUID.fromString(blogId), createPostOfBlogDto);
        return PostMapper.toView(createdPost);
    }

    @GetMapping("/{blogId}/posts")
    @ResponseStatus(HttpStatus.OK)
    public PaginationDto<ViewPostDto> findUserPosts(@PathVariable String blogId,
                                                    CommonQueryParamsDto commonQueryParamsDto,
                                                    @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return postQueryRepository.findPostsOfBlogByUserId(blogId, commonQueryParamsDto, customUserDetails.getUserId());
    }

    @PutMapping("/{blogId}/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePost(@PathVariable String blogId,
                                        @PathVariable String postId,
                                        @RequestBody @Valid UpdatePostOfBlogDto updatePostOfBlogDto,
                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        postService.updateWithBlogId(customUserDetails.getUserId(), postId, blogId, updatePostOfBlogDto);
    }

    @DeleteMapping("/{blogId}/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePost(@PathVariable String blogId,
                                        @PathVariable String postId,
                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        postService.removeWithBlogId(customUserDetails.getUserId(), postId, blogId);
    }

    @GetMapping("/comments")
    @ResponseStatus(HttpStatus.OK)
    public PaginationDto<ViewBloggerCommentDto> findCommentsOfUserBlog(CommonQueryParamsDto commonQueryParamsDto,
                                                    @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return commentQueryRepository.findCommentsOfUserBlogs(UUID.fromString(customUserDetails.getUserId()), commonQueryParamsDto);
    }
}
