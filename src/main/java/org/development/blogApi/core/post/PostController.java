package org.development.blogApi.core.post;

import jakarta.validation.Valid;
import org.development.blogApi.common.dto.CommonQueryParamsDto;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.core.blog.dto.response.ViewBlogDto;
import org.development.blogApi.core.comment.dto.response.ViewPublicCommentDto;
import org.development.blogApi.core.comment.repository.CommentQueryRepository;
import org.development.blogApi.core.comment.CommentService;
import org.development.blogApi.core.comment.dto.request.CreateCommentDto;
import org.development.blogApi.core.comment.entity.Comment;
import org.development.blogApi.core.comment.utils.CommentMapper;
import org.development.blogApi.core.like.dto.request.UpdateLikeDto;
import org.development.blogApi.core.post.dto.response.ViewPostDto;
import org.development.blogApi.core.post.entity.Post;
import org.development.blogApi.core.post.repository.PostQueryRepository;
import org.development.blogApi.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
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


    @GetMapping
    public ResponseEntity<?> findAllPosts(
            CommonQueryParamsDto query,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        PaginationDto<ViewPostDto> paginationDto = this.postsQueryRepository.findAllPosts(query, customUserDetails.getUserId());
        return new ResponseEntity<>(paginationDto, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> findOnePost(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Optional<ViewPostDto> optionalViewPostDto = this.postsQueryRepository.findOnePost(id, customUserDetails.getUserId());
        if (optionalViewPostDto.isEmpty()) {
            return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalViewPostDto.get(), HttpStatus.OK);
    }

    // TODO delete
//    @PostMapping("/{id}/comments")
//    public ResponseEntity<?> createCommentOfPost(
//            @PathVariable String id,
//            @Valid @RequestBody CreateCommentDto createCommentDto,
//            @AuthenticationPrincipal CustomUserDetails customUserDetails
//    ) {
//        if (customUserDetails == null) {
//            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
//        }
//
//        Comment comment = commentService.create(createCommentDto, id, customUserDetails.getUserId());
//        return ResponseEntity.ok(CommentMapper.toPublicViewFromDomain(comment));
//    }

    // TODO change all ResponseEntity<?> to another implementation
    @GetMapping("/{id}/comments")
    public ResponseEntity<?> findCommentsOfPost(
            @PathVariable String id,
            @Valid CommonQueryParamsDto query,
            @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Post post = postService.findById(UUID.fromString(id));
        if (post == null) {
            return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
        }

        PaginationDto<ViewPublicCommentDto> commentDtoPaginationDto = commentQueryRepository.findCommentsOfPost(
                UUID.fromString(id),
                query,
                UUID.fromString(customUserDetails.getUserId())
        );
        return new ResponseEntity<>(commentDtoPaginationDto, HttpStatus.OK);
    }

    // TODO add @Valid to all DTOs objects
    @PostMapping("/{id}/comments")
    public ResponseEntity<?> createCommentsOfPost(
            @PathVariable String id,
            @RequestBody @Valid CreateCommentDto createCommentDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Post post = postService.findById(UUID.fromString(id));
        if (post == null) {
            return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
        }

        Comment comment = commentService.create(createCommentDto, id, customUserDetails.getUserId());
        return new ResponseEntity<>(CommentMapper.toPublicViewFromDomain(comment), HttpStatus.OK);
    }

    @PutMapping("/{id}/like-status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> updateLikeStatus(
            @PathVariable String id,
            @RequestBody @Valid UpdateLikeDto updateLikeDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        postService.updateLikeStatus(
                UUID.fromString(id),
                customUserDetails.getUserId(),
                customUserDetails.getUsername(),
                updateLikeDto.getLikeStatus()
        );

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

