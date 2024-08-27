package org.development.blogApi.core.comment;

import org.development.blogApi.core.comment.dto.request.UpdateCommentDto;
import org.development.blogApi.core.comment.entity.Comment;
import org.development.blogApi.core.comment.repository.CommentQueryRepository;
import org.development.blogApi.core.comment.utils.CommentMapper;
import org.development.blogApi.core.like.dto.request.UpdateLikeDto;
import org.development.blogApi.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentQueryRepository commentQueryRepository;
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentQueryRepository commentQueryRepository, CommentService commentService) {
        this.commentQueryRepository = commentQueryRepository;
        this.commentService = commentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserCommentById(@PathVariable("id") String id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        System.out.println("customUserDetails: " + customUserDetails);
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Comment comment = this.commentQueryRepository.findByIdAndUserId(UUID.fromString(id), UUID.fromString(customUserDetails.getUserId()))
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        return new ResponseEntity<>(CommentMapper.toPublicViewFromDomain(comment), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable("id") String id,
            @RequestBody UpdateCommentDto updateCommentDto,
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
    public ResponseEntity<?> updateLikeStatus(
            @PathVariable("id") String id,
            @RequestBody UpdateLikeDto updateLikeDto,
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
    public ResponseEntity<?> remove(@PathVariable("id") String id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        System.out.println("customUserDetails: " + customUserDetails);
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        commentService.remove(id, customUserDetails.getUserId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
