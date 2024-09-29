package org.development.blogApi.core.comment;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import org.development.blogApi.core.comment.dto.request.UpdateCommentDto;
import org.development.blogApi.core.comment.dto.response.ViewPublicCommentDto;
import org.development.blogApi.core.comment.repository.CommentQueryRepository;
import org.development.blogApi.core.like.dto.request.UpdateLikeDto;
import org.development.blogApi.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/comments")
public class CommentController {
    private final CommentQueryRepository commentQueryRepository;
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentQueryRepository commentQueryRepository, CommentService commentService) {
        this.commentQueryRepository = commentQueryRepository;
        this.commentService = commentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findUserCommentById(@PathVariable("id") String id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        ViewPublicCommentDto viewPublicCommentDto = this.commentQueryRepository.findCommentByIdAndUserId(UUID.fromString(id), UUID.fromString(customUserDetails.getUserId()))
                .orElse(null);

        return new ResponseEntity<>(viewPublicCommentDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable("id") String id,
            @RequestBody @Valid UpdateCommentDto updateCommentDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        System.out.println("customUserDetails: " + customUserDetails);
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        commentService.updateComment(id, customUserDetails.getUserId(), updateCommentDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/like-status")
    public ResponseEntity<?> updateCommentLikeStatus(
            @PathVariable("id") String id,
            @RequestBody @Valid UpdateLikeDto updateLikeDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        System.out.println("customUserDetails: " + customUserDetails);
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        commentService.updateLikeStatus(id, customUserDetails.getUserId(), customUserDetails.getUsername(), updateLikeDto.getLikeStatus());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") String id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        System.out.println("customUserDetails: " + customUserDetails);
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        commentService.remove(id, customUserDetails.getUserId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
