package org.development.blogApi.modules.quiz.pairQuizGame;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.GamePairQueryParams;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.TopUsersQueryParams;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.request.AnswerQuestionDto;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.response.ViewAnswerDto;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.response.ViewGamePairDto;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.response.ViewMyStatisticDto;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.response.ViewUserStatisticDto;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.GamePairEntity;
import org.development.blogApi.modules.quiz.pairQuizGame.exceptions.GamePairNotFoundException;
import org.development.blogApi.modules.quiz.pairQuizGame.repository.QuizGamePairQueryRepository;
import org.development.blogApi.modules.quiz.pairQuizGame.service.QuizGamePairService;
import org.development.blogApi.modules.quiz.pairQuizGame.utils.GamePairMapper;
import org.development.blogApi.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/pair-game-quiz")
@AllArgsConstructor
public class QuizGamePairController {

    private final QuizGamePairService quizGamePairService;
    private final QuizGamePairQueryRepository quizGamePairQueryRepository;

    @GetMapping("/users/top")
    public PaginationDto<ViewUserStatisticDto> getTopUsersStatistic(TopUsersQueryParams topUsersQueryParams) {
        System.out.println(topUsersQueryParams);
        return this.quizGamePairService.getTopUsersStatistic(topUsersQueryParams);
    }

    @GetMapping("/users/my-statistic")
    public ViewMyStatisticDto getStatisticByUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return this.quizGamePairService.getCurrentUserStatistic(userDetails.getUserId());
    }

    @GetMapping("/pairs/my")
    public PaginationDto<ViewGamePairDto> getAllGamePairByUser(@AuthenticationPrincipal CustomUserDetails userDetails, GamePairQueryParams gamePairQueryParams) {
        return this.quizGamePairQueryRepository.findAllGamePairs(gamePairQueryParams, userDetails.getUserId());
    }

    @GetMapping("/pairs/my-current")
    public ViewGamePairDto getCurrentUserGamePair(@AuthenticationPrincipal CustomUserDetails userDetails) {
        GamePairEntity foundedGamePair = this.quizGamePairService.getCurrentUserGamePair(userDetails.getUserId())
                .orElseThrow(() -> new GamePairNotFoundException());

        return GamePairMapper.toView(foundedGamePair);
    }

    @GetMapping("/pairs/{id}")
    public ViewGamePairDto getGamePairById(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           @PathVariable String id) {
        GamePairEntity foundedGamePair = this.quizGamePairService.getGamePairByIdAndParticipantUser(userDetails.getUserId(), id);
        return GamePairMapper.toView(foundedGamePair);
    }

    @PostMapping("/pairs/connection")
    public ViewGamePairDto getConnectUserToGamePair(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return GamePairMapper.toView(this.quizGamePairService.connectUserToGamePair(userDetails.getUserId()));
    }

    @PostMapping("/pairs/my-current/answers")
    public ViewAnswerDto getConnectUserToGamePair(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @RequestBody @Valid AnswerQuestionDto answerQuestionDto) {
        return GamePairMapper.answerEntityToView(this.quizGamePairService.answerGameQuestion(userDetails.getUserId(), answerQuestionDto));
    }

}
