package org.development.blogApi.core.comment;

import org.development.blogApi.core.comment.dto.request.CreateCommentDto;
import org.development.blogApi.core.comment.dto.request.UpdateCommentDto;
import org.development.blogApi.core.comment.entity.Comment;
import org.development.blogApi.core.comment.repository.CommentRepository;
import org.development.blogApi.core.like.LikeService;
import org.development.blogApi.core.like.enums.LikeLocation;
import org.development.blogApi.core.like.enums.LikeStatus;
import org.development.blogApi.core.post.repository.PostRepository;
import org.development.blogApi.core.post.entity.Post;
import org.development.blogApi.exceptions.commentExceptions.CommentNotFoundException;
import org.development.blogApi.exceptions.commentExceptions.CommentUpdateForbiddenException;
import org.development.blogApi.exceptions.postExceptions.PostNotFoundException;
import org.development.blogApi.exceptions.userExceptions.UserNotFoundException;
import org.development.blogApi.user.repository.UserRepository;
import org.development.blogApi.user.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeService likeService;

//    private final BloggerBanInfoRepository bloggerBanInfoRepository;

    @Autowired
    public CommentService(
            CommentRepository commentRepository,
            PostRepository postRepository,
            UserRepository userRepository,
            LikeService likeService
//            BloggerBanInfoRepository bloggerBanInfoRepository
    ) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeService = likeService;
//        this.bloggerBanInfoRepository = bloggerBanInfoRepository;
    }

    public Comment create(CreateCommentDto createCommentDto, String postId, String userId) {
        Post commentedPost = postRepository.findById(UUID.fromString(postId)).orElseThrow(() -> new PostNotFoundException());
        UserEntity commentCreator = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException());
        Comment newComment = Comment.createInstance(createCommentDto, commentCreator, commentedPost);
        return commentRepository.save(newComment);
    }

    public Optional<Comment> findOne(String id) {
        return commentRepository.findById(UUID.fromString(id));
    }

    public void updateComment(String id, String userId, UpdateCommentDto updateCommentDto) {
        Comment comment = commentRepository.findById(UUID.fromString(id)).orElseThrow(() -> new CommentNotFoundException());
        if (!comment.getUser().getId().toString().equals(userId)) {
            throw new CommentUpdateForbiddenException("User can not update not his comment");
        }

        comment.setContent(updateCommentDto.getContent());
        commentRepository.save(comment);
    }

    public void updateLikeStatus(String id, String userId, String userLogin, LikeStatus status) {
        Comment comment = commentRepository.findById(UUID.fromString(id)).orElseThrow(() -> new CommentNotFoundException());
        likeService.like(userId, userLogin, LikeLocation.COMMENT, comment.getId().toString(), status);
    }

    public void remove(String id, String userId) {
        Comment comment = commentRepository.findById(UUID.fromString(id)).orElseThrow(() -> new CommentNotFoundException());
        if (!comment.getUser().getId().toString().equals(userId)) {
            throw new CommentUpdateForbiddenException("Comment can not be deleted by another user");
        }

        commentRepository.deleteById(UUID.fromString(id));
    }

    public void removeAllComments() {
        commentRepository.deleteAll();
    }

//    public boolean setBanStatusToUserComments(String userId, boolean isBanned) {
//        return commentsRepository.setBanStatusToUserComments(userId, isBanned);
//    }
}
