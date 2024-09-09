package org.development.blogApi.deleteData;

import org.development.blogApi.core.blog.repository.BlogRepository;
import org.development.blogApi.core.comment.repository.CommentRepository;
import org.development.blogApi.core.like.repository.LikeRepository;
import org.development.blogApi.core.post.repository.PostRepository;
import org.development.blogApi.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("api/testing")
public class DeleteDataController {

    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    public DeleteDataController(LikeRepository likeRepository,
                                CommentRepository commentRepository,
                                PostRepository postRepository,
                                BlogRepository blogRepository,
                                UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllData() {
        likeRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        blogRepository.deleteAll();
        userRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
