package org.development.blogApi.core.comment;

import jakarta.validation.Valid;
import org.development.blogApi.core.comment.dto.request.UpdateCommentDto;
import org.development.blogApi.core.comment.dto.response.ViewPublicCommentDto;
import org.development.blogApi.core.comment.repository.CommentQueryRepository;
import org.development.blogApi.core.like.dto.request.UpdateLikeDto;
import org.development.blogApi.core.like.enums.LikeStatus;
import org.development.blogApi.security.CustomUserDetails;
import org.development.blogApi.security.annotation.GetUserFromJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetUserFromJwt
    @GetMapping("/{id}")
    public ResponseEntity<?> findUserCommentById(@PathVariable("id") String id) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID userId = customUserDetails != null ? UUID.fromString(customUserDetails.getUserId()) : null;

        ViewPublicCommentDto viewPublicCommentDto = this.commentQueryRepository.findCommentByIdAndUserId(UUID.fromString(id), userId).orElse(null);
        if (viewPublicCommentDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(viewPublicCommentDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable("id") String id,
            @RequestBody @Valid UpdateCommentDto updateCommentDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
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
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        System.out.println("C " + updateLikeDto.getLikeStatus());
        commentService.updateLikeStatus(id, customUserDetails.getUserId(), customUserDetails.getUsername(), LikeStatus.fromValue(updateLikeDto.getLikeStatus()));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") String id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        commentService.remove(id, customUserDetails.getUserId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
