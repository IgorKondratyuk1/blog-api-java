package org.development.blogApi.modules.quiz.pairQuizGame;

import lombok.AllArgsConstructor;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.request.AnswerQuestionDto;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.response.ViewAnswerDto;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.response.ViewGamePairDto;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.GamePairEntity;
import org.development.blogApi.modules.quiz.pairQuizGame.exceptions.GamePairNotFoundException;
import org.development.blogApi.modules.quiz.pairQuizGame.utils.GamePairMapper;
import org.development.blogApi.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/pair-game-quiz/pairs")
@AllArgsConstructor
public class QuizGamePairController {

    private final QuizGamePairService quizGamePairService;

    @GetMapping("/my-current")
    public ViewGamePairDto getCurrentUserGamePair(@AuthenticationPrincipal CustomUserDetails userDetails) {
        GamePairEntity foundedGamePair = this.quizGamePairService.getCurrentUserGamePair(userDetails.getUserId())
                .orElseThrow(() -> new GamePairNotFoundException());

        return GamePairMapper.toView(foundedGamePair);
    }

    @GetMapping("/{id}")
    public ViewGamePairDto getGamePairById(@PathVariable String id) {
        GamePairEntity foundedGamePair = this.quizGamePairService.getGamePairById(id)
                .orElseThrow(() -> new GamePairNotFoundException());

        return GamePairMapper.toView(foundedGamePair);
    }

    @PostMapping("/connect")
    public ViewGamePairDto getConnectUserToGamePair(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return GamePairMapper.toView(this.quizGamePairService.connectUserToGamePair(userDetails.getUserId()));
    }

    @PostMapping("/my-current/answers")
    public ViewAnswerDto getConnectUserToGamePair(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody AnswerQuestionDto answerQuestionDto) {
        return GamePairMapper.answerEntityToView(this.quizGamePairService.answerGameQuestion(userDetails.getUserId(), answerQuestionDto));
    }

}
