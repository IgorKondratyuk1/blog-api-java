package org.development.blogApi.superAdmin;

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
public class SuperAdminBlogsController {

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
        Blog blog = blogService.create(null, createBlogDto);
        return new ResponseEntity(BlogMapper.toView(blog), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBlog(@PathVariable("id") String id,
                                           @RequestBody @Valid UpdateBlogDto updateBlogDto) {
        blogService.update(null, UUID.fromString(id), updateBlogDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> removeBlog(@PathVariable("id") String id) {
        blogService.removeByAdmin(UUID.fromString(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{blogId}/posts")
    public ResponseEntity<ViewPostDto> createPostOfBlog(@PathVariable("blogId") String blogId,
                                                        @RequestBody @Valid CreatePostOfBlogDto createPostOfBlogDto) {
        ViewPostDto viewPostDto = postService.createByAdmin(UUID.fromString(blogId), createPostOfBlogDto);
        return new ResponseEntity<>(viewPostDto, HttpStatus.OK);
    }

    @GetMapping("/{blogId}/posts")
    public ResponseEntity<PaginationDto<ViewPostDto>> findUserPosts(@PathVariable("blogId") String blogId,
                                                                    CommonQueryParamsDto query) {
        PaginationDto<ViewPostDto> viewPostDto = postQueryRepository.findPostsOfBlog(blogId, query, null);
        return new ResponseEntity<>(viewPostDto, HttpStatus.OK);
    }

    @PutMapping("/{blogId}/posts/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable("blogId") String blogId,
                                           @PathVariable("postId") String postId,
                                           @RequestBody @Valid UpdatePostOfBlogDto updatePostOfBlogDto) {
        postService.updateWithBlogIdByAdmin(postId, blogId, updatePostOfBlogDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{blogId}/posts/{postId}")
    public ResponseEntity<Void> removePost(@PathVariable("blogId") String blogId,
                                           @PathVariable("postId") String postId) {
        postService.removeWithBlogIdByAdmin(postId, blogId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{blogId}/bind-with-user/{userId}")
    public ResponseEntity<Void> bindWithUser(@PathVariable("blogId") String blogId,
                                             @PathVariable("userId") String userId) {
        blogService.bindBlogWithUser(UUID.fromString(userId), UUID.fromString(blogId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

//    @PutMapping("/{blogId}/ban")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public ResponseEntity<Void> banBlog(@PathVariable("blogId") String blogId, @RequestBody BanBlogDto banBlogDto) {
//        Object result = commandBus.execute(new BanBlogCommand(blogId, banBlogDto));
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
}
