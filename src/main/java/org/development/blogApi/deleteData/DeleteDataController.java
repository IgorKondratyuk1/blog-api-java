package org.development.blogApi.deleteData;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.development.blogApi.core.blog.entity.Blog;
import org.development.blogApi.core.blog.repository.BlogRepository;
import org.development.blogApi.core.comment.repository.CommentRepository;
import org.development.blogApi.core.like.repository.LikeRepository;
import org.development.blogApi.core.post.repository.PostRepository;
import org.development.blogApi.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("api/testing")
public class DeleteDataController {

    @PersistenceContext
    private EntityManager entityManager;
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

    @Transactional
    @DeleteMapping("/all-data")
    public ResponseEntity<Void> deleteAllData() {
        likeRepository.deleteAllCommentLikes();
        likeRepository.deleteAllPostLikes();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        blogRepository.deleteAll();
        userRepository.deleteAll();


//        String jpql = "DELETE FROM UserEntity.roles";
//        TypedQuery<Blog> query = entityManager.createQuery(jpql, Blog.class);
//        System.out.println(query.executeUpdate());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
