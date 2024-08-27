package org.development.blogApi.core.post;

import jakarta.validation.Valid;
import org.development.blogApi.common.dto.CommonQueryParamsDto;
import org.development.blogApi.core.comment.repository.CommentQueryRepository;
import org.development.blogApi.core.comment.CommentService;
import org.development.blogApi.core.comment.dto.request.CreateCommentDto;
import org.development.blogApi.core.comment.entity.Comment;
import org.development.blogApi.core.comment.utils.CommentMapper;
import org.development.blogApi.core.like.dto.request.UpdateLikeDto;
import org.development.blogApi.core.post.repository.PostQueryRepository;
import org.development.blogApi.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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


    @GetMapping
    public ResponseEntity<?> findAll(
            CommonQueryParamsDto query) {
        String userId = "1"; // TODO
        return ResponseEntity.ok(postsQueryRepository.findAll());
    }


//    @GetMapping("/{id}")
//    public ResponseEntity<?> findOne(
//            @PathVariable String id,
//            @AuthenticationPrincipal CustomUserDetails customUserDetails)
//    {
//        System.out.println("customUserDetails: " + customUserDetails);
//        if (customUserDetails == null) {
//            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
//        }
//        Post post = this.postsQueryRepository.findOne(id, customUserDetails.getUserId());
//        if (post == null) {
//            throw new NotFoundException();
//        }
//        return ResponseEntity.ok(post);
//    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> createCommentOfPost(
            @PathVariable String id,
            @Valid @RequestBody CreateCommentDto createCommentDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        System.out.println("customUserDetails: " + customUserDetails);
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Comment comment = commentService.create(createCommentDto, id, customUserDetails.getUserId());
        return ResponseEntity.ok(CommentMapper.toPublicViewFromDomain(comment));
    }

//    @GetMapping("/{id}/comments")
//    public ResponseEntity<?> findCommentsOfPost(
//            @PathVariable String id,
//            @Valid CommonQueryParamsDto query,
//            @AuthenticationPrincipal CustomUserDetails customUserDetails)
//    {
//        System.out.println("customUserDetails: " + customUserDetails);
//        if (customUserDetails == null) {
//            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
//        }
//
//        Post post = postService.findById(UUID.fromString(id));
//        if (post == null) {
//            return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
//        }
//
//        List<Comment> comment = commentQueryRepository.findCommentsOfPost(id, query, customUserDetails.getUserId());
//        return new ResponseEntity<>(comment, HttpStatus.OK);
//    }

    @PutMapping("/{id}/like-status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> updateLikeStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateLikeDto updateLikeDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
        System.out.println("customUserDetails: " + customUserDetails);
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

