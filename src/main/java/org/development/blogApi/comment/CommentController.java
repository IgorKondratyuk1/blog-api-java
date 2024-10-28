package org.development.blogApi.comment;

import jakarta.validation.Valid;
import org.development.blogApi.comment.dto.request.UpdateCommentDto;
import org.development.blogApi.comment.dto.response.ViewPublicCommentDto;
import org.development.blogApi.comment.repository.CommentQueryRepository;
import org.development.blogApi.like.dto.request.UpdateLikeDto;
import org.development.blogApi.like.enums.LikeStatus;
import org.development.blogApi.comment.exceptions.CommentNotFoundException;
import org.development.blogApi.infrastructure.security.CustomUserDetails;
import org.development.blogApi.infrastructure.security.annotation.GetUserFromJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
    public ViewPublicCommentDto findUserCommentById(@PathVariable("id") String id) {
        UUID userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getName() != "anonymousUser") {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            userId = UUID.fromString(customUserDetails.getUserId());
        }

        return this.commentQueryRepository.findCommentByIdAndUserId(UUID.fromString(id), userId).orElseThrow(() -> new CommentNotFoundException());
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateComment(@PathVariable("id") String id,
                                           @RequestBody @Valid UpdateCommentDto updateCommentDto,
                                           @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
//        if (customUserDetails == null) {
//            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
//        }
        commentService.updateComment(id, customUserDetails.getUserId(), updateCommentDto);
    }

    @PutMapping("/{id}/like-status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCommentLikeStatus(@PathVariable("id") String id,
                                                     @RequestBody @Valid UpdateLikeDto updateLikeDto,
                                                     @AuthenticationPrincipal CustomUserDetails customUserDetails)
    {
//        if (customUserDetails == null) {
//            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
//        }
        commentService.updateLikeStatus(id, customUserDetails.getUserId(), customUserDetails.getUsername(), LikeStatus.fromValue(updateLikeDto.getLikeStatus()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("id") String id, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
//        if (customUserDetails == null) {
//            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
//        }
        commentService.remove(id, customUserDetails.getUserId());
    }
}
