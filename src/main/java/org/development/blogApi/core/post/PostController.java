package org.development.blogApi.core.post;

import jakarta.validation.Valid;
import org.development.blogApi.common.dto.CommonQueryParamsDto;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.core.comment.CommentService;
import org.development.blogApi.core.comment.dto.request.CreateCommentDto;
import org.development.blogApi.core.comment.dto.response.ViewPublicCommentDto;
import org.development.blogApi.core.comment.entity.Comment;
import org.development.blogApi.core.comment.repository.CommentQueryRepository;
import org.development.blogApi.core.comment.utils.CommentMapper;
import org.development.blogApi.core.like.dto.request.UpdateLikeDto;
import org.development.blogApi.core.like.enums.LikeStatus;
import org.development.blogApi.core.post.dto.response.ViewPostDto;
import org.development.blogApi.core.post.repository.PostQueryRepository;
import org.development.blogApi.core.post.exceptions.PostNotFoundException;
import org.development.blogApi.security.CustomUserDetails;
import org.development.blogApi.security.annotation.GetUserFromJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/posts")
public class PostController {
    private final PostQueryRepository postsQueryRepository;
    private final PostService postService;
    private final CommentQueryRepository commentQueryRepository;
    private final CommentService commentService;

    @Autowired
    public PostController(
            PostQueryRepository postsQueryRepository,
            PostService postService,
            CommentQueryRepository commentQueryRepository,
            CommentService commentService
    ) {
        this.postsQueryRepository = postsQueryRepository;
        this.postService = postService;
        this.commentQueryRepository = commentQueryRepository;
        this.commentService = commentService;
    }


    @GetUserFromJwt
    @GetMapping
    public PaginationDto<ViewPostDto> findAllPosts(CommonQueryParamsDto query) {
        // TODO refactor
        String userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getName() != "anonymousUser") {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            userId = customUserDetails.getUserId();
        }

        // TODO refactor all QueryRepositories to one type of params
        return this.postsQueryRepository.findAllPosts(query, userId);
    }

    @GetUserFromJwt
    @GetMapping("/{id}")
    public ViewPostDto findPostById(@PathVariable String id) {
        // TODO refactor AND why not work: @AuthenticationPrincipal CustomUserDetails customUserDetails
        String userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getName() != "anonymousUser") {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            userId = customUserDetails.getUserId();
        }

        return this.postsQueryRepository.findOnePost(id, userId).orElseThrow(() -> new PostNotFoundException());
    }

    @GetUserFromJwt
    @GetMapping("/{id}/comments")
    public PaginationDto<ViewPublicCommentDto> findCommentsOfPost(@PathVariable String id, CommonQueryParamsDto query) {
        UUID userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getName() != "anonymousUser") {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            userId = UUID.fromString(customUserDetails.getUserId());
        }

        postService.findById(UUID.fromString(id)); // Check that post exist
        return commentQueryRepository.findCommentsOfPost(
                UUID.fromString(id),
                query,
                userId
        );
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ViewPublicCommentDto createCommentsOfPost(
            @PathVariable String id,
            @RequestBody @Valid CreateCommentDto createCommentDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        Comment comment = commentService.create(createCommentDto, id, customUserDetails.getUserId());
        return CommentMapper.toPublicViewFromDomain(comment);
    }

    @PutMapping("/{id}/like-status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLikeStatus(@PathVariable String id,
                                 @RequestBody @Valid UpdateLikeDto updateLikeDto,
                                 @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        postService.updateLikeStatus(
                UUID.fromString(id),
                customUserDetails.getUserId(),
                customUserDetails.getUsername(),
                LikeStatus.fromValue(updateLikeDto.getLikeStatus())
        );
    }
}

