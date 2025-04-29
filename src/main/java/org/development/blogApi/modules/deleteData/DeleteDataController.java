package org.development.blogApi.modules.deleteData;

import lombok.RequiredArgsConstructor;
import org.development.blogApi.modules.blogPlatform.core.blog.repository.BlogRepository;
import org.development.blogApi.modules.blogPlatform.core.comment.repository.CommentRepository;
import org.development.blogApi.modules.blogPlatform.core.like.repository.LikeRepository;
import org.development.blogApi.modules.blogPlatform.core.post.repository.PostRepository;
import org.development.blogApi.modules.quiz.pairQuizGame.repository.QuizGamePairRepository;
import org.development.blogApi.modules.quiz.question.repository.QuizQuestionRepository;
import org.development.blogApi.modules.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/testing")
@RequiredArgsConstructor
public class DeleteDataController {
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizGamePairRepository quizGamePairRepository;


    @DeleteMapping("/all-data")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteAllData() {
        likeRepository.deleteAllCommentLikes();
        likeRepository.deleteAllPostLikes();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        blogRepository.deleteAll();
        quizGamePairRepository.deleteAll();
        quizQuestionRepository.deleteAll();
        userRepository.deleteAll();
    }
}
