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
import org.development.blogApi.exceptions.postExceptions.PostNotFoundException;
import org.development.blogApi.exceptions.userExceptions.UserNotFoundException;
import org.development.blogApi.security.CustomUserDetails;
import org.development.blogApi.security.annotation.GetUserFromJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
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
    public ResponseEntity<PaginationDto<ViewPostDto>> findAllPosts(
            CommonQueryParamsDto query,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String userId = customUserDetails != null ? customUserDetails.getUserId() : null;

        PaginationDto<ViewPostDto> paginationDto = this.postsQueryRepository.findAllPosts(query, userId);
        return new ResponseEntity<>(paginationDto, HttpStatus.OK);
    }

    @GetUserFromJwt
    @GetMapping("/{id}")
    public ResponseEntity<ViewPostDto> findPostById(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String userId = customUserDetails != null ? customUserDetails.getUserId() : null;

        Optional<ViewPostDto> optionalViewPostDto = this.postsQueryRepository.findOnePost(id, userId);
        if (optionalViewPostDto.isEmpty()) {
            throw  new PostNotFoundException();
        }

        return new ResponseEntity<>(optionalViewPostDto.get(), HttpStatus.OK);
    }

    // TODO change all ResponseEntity<?> to another implementation
    @GetUserFromJwt
    @GetMapping("/{id}/comments")
    public ResponseEntity<PaginationDto<ViewPublicCommentDto>> findCommentsOfPost(
            @PathVariable String id,
            CommonQueryParamsDto query,
            @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        String userId = customUserDetails != null ? customUserDetails.getUserId() : null;

        postService.findById(UUID.fromString(id)); // Check that post exist
        PaginationDto<ViewPublicCommentDto> commentDtoPaginationDto = commentQueryRepository.findCommentsOfPost(
                UUID.fromString(id),
                query,
                UUID.fromString(userId)
        );
        return new ResponseEntity<>(commentDtoPaginationDto, HttpStatus.OK);
    }


    @PostMapping("/{id}/comments")
    public ResponseEntity<ViewPublicCommentDto> createCommentsOfPost(
            @PathVariable String id,
            @RequestBody @Valid CreateCommentDto createCommentDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        Comment comment = commentService.create(createCommentDto, id, customUserDetails.getUserId());
        return new ResponseEntity<>(CommentMapper.toPublicViewFromDomain(comment), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/like-status")
    public ResponseEntity<Void> updateLikeStatus(
            @PathVariable String id,
            @RequestBody @Valid UpdateLikeDto updateLikeDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        postService.updateLikeStatus(
                UUID.fromString(id),
                customUserDetails.getUserId(),
                customUserDetails.getUsername(),
                LikeStatus.fromValue(updateLikeDto.getLikeStatus())
        );

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

