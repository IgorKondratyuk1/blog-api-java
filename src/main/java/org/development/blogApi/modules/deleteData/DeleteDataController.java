package org.development.blogApi.modules.deleteData;

import org.development.blogApi.modules.blogPlatform.core.blog.repository.BlogRepository;
import org.development.blogApi.modules.blogPlatform.core.comment.repository.CommentRepository;
import org.development.blogApi.modules.blogPlatform.core.like.repository.LikeRepository;
import org.development.blogApi.modules.blogPlatform.core.post.repository.PostRepository;
import org.development.blogApi.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/testing")
public class DeleteDataController {
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    @Autowired
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

    @DeleteMapping("/all-data")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteAllData() {
        likeRepository.deleteAllCommentLikes();
        likeRepository.deleteAllPostLikes();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        blogRepository.deleteAll();
        userRepository.deleteAll();
    }
}
