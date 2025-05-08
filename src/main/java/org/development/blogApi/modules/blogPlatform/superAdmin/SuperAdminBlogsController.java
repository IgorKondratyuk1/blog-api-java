package org.development.blogApi.modules.blogPlatform.superAdmin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.development.blogApi.common.dto.CommonQueryParamsDto;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.modules.blogPlatform.core.blog.BlogService;
import org.development.blogApi.modules.blogPlatform.core.blog.dto.request.CreateBlogDto;
import org.development.blogApi.modules.blogPlatform.core.blog.dto.request.UpdateBlogDto;
import org.development.blogApi.modules.blogPlatform.core.blog.dto.response.ViewBlogDto;
import org.development.blogApi.modules.blogPlatform.core.blog.dto.response.ViewExtendedBlogDto;
import org.development.blogApi.modules.blogPlatform.core.blog.entity.Blog;
import org.development.blogApi.modules.blogPlatform.core.blog.repository.BlogQueryRepository;
import org.development.blogApi.modules.blogPlatform.core.blog.utils.BlogMapper;
import org.development.blogApi.modules.blogPlatform.core.post.PostService;
import org.development.blogApi.modules.blogPlatform.core.post.dto.request.CreatePostOfBlogDto;
import org.development.blogApi.modules.blogPlatform.core.post.dto.request.UpdatePostOfBlogDto;
import org.development.blogApi.modules.blogPlatform.core.post.dto.response.ViewPostDto;
import org.development.blogApi.modules.blogPlatform.core.post.repository.PostQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/sa/blogs")
@RequiredArgsConstructor
public class SuperAdminBlogsController {

    private final BlogService blogService;
    private final PostService postService;
    private final BlogQueryRepository blogQueryRepository;
    private final PostQueryRepository postQueryRepository;

    @GetMapping("")
    public PaginationDto<ViewExtendedBlogDto> findAll(CommonQueryParamsDto query) {
        return blogQueryRepository.findBlogsWithExtendedInfo(query);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ViewBlogDto createBlog(@RequestBody @Valid CreateBlogDto createBlogDto) {
        Blog blog = blogService.createByAdmin(createBlogDto);
        return BlogMapper.toView(blog);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBlog(@PathVariable("id") UUID id, @RequestBody @Valid UpdateBlogDto updateBlogDto) {
        blogService.updateByAdmin(id, updateBlogDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBlog(@PathVariable("id") String id) {
        blogService.removeByAdmin(UUID.fromString(id));
    }

    @PostMapping("/{blogId}/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public ViewPostDto createPostOfBlog(@PathVariable("blogId") UUID blogId,
                                                        @RequestBody @Valid CreatePostOfBlogDto createPostOfBlogDto) {
        return postService.createByAdmin(blogId, createPostOfBlogDto);
    }

    @GetMapping("/{blogId}/posts")
    public PaginationDto<ViewPostDto> findUserPosts(@PathVariable("blogId") String blogId, CommonQueryParamsDto query) {
        return postQueryRepository.findPostsOfBlog(blogId, query, null);
    }

    // TODO: Change to UUID (@PathVariable("blogId") String blogId)
    @PutMapping("/{blogId}/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePost(@PathVariable("blogId") String blogId,
                           @PathVariable("postId") String postId,
                           @RequestBody @Valid UpdatePostOfBlogDto updatePostOfBlogDto) {
        postService.updateWithBlogIdByAdmin(postId, blogId, updatePostOfBlogDto);
    }

    @DeleteMapping("/{blogId}/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePost(@PathVariable("blogId") String blogId, @PathVariable("postId") String postId) {
        postService.removeWithBlogIdByAdmin(postId, blogId);
    }

    @PutMapping("/{blogId}/bind-with-user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void bindWithUser(@PathVariable("blogId") UUID blogId, @PathVariable("userId") UUID userId) {
        blogService.bindBlogWithUser(userId, blogId);
    }

//    @PutMapping("/{blogId}/ban")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public ResponseEntity<Void> banBlog(@PathVariable("blogId") String blogId, @RequestBody BanBlogDto banBlogDto) {
//        Object result = commandBus.execute(new BanBlogCommand(blogId, banBlogDto));
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
}
