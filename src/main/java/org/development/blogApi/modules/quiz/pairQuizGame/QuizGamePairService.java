package org.development.blogApi.modules.quiz.pairQuizGame;

import lombok.RequiredArgsConstructor;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.request.AnswerQuestionDto;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.AnswerEntity;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.GamePairEntity;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.GamePlayerProgressEntity;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.enums.AnswerStatus;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.enums.GamePairStatus;
import org.development.blogApi.modules.quiz.pairQuizGame.exceptions.GamePairNotFoundException;
import org.development.blogApi.modules.quiz.pairQuizGame.repository.AnswerRepository;
import org.development.blogApi.modules.quiz.pairQuizGame.repository.GamePlayerProgressRepository;
import org.development.blogApi.modules.quiz.pairQuizGame.repository.QuizGamePairRepository;
import org.development.blogApi.modules.quiz.question.QuizQuestionService;
import org.development.blogApi.modules.quiz.question.entity.QuizQuestionEntity;
import org.development.blogApi.modules.user.UserService;
import org.development.blogApi.modules.user.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class QuizGamePairService {

    private final QuizGamePairRepository quizGamePairRepository;

    private final QuizQuestionService quizQuestionService;

    private final GamePlayerProgressRepository gamePlayerProgressRepository;

    private final AnswerRepository answerRepository;

    private final UserService userService;

    public GamePairEntity getGamePairById(String gamePairId) {
        return this.quizGamePairRepository.findById(UUID.fromString(gamePairId))
                .orElseThrow(() -> new GamePairNotFoundException());
    }

    public GamePairEntity getCurrentUserGamePair(String userId) {
        return this.quizGamePairRepository.findGameByUserId(UUID.fromString(userId))
                .orElseThrow(() -> new GamePairNotFoundException());
    }

    public GamePairEntity connectUserToGamePair(String userId) {
        UserEntity user = this.userService.findById(UUID.fromString(userId));

        // TODO check
        Optional<GamePairEntity> gamePairWithOnePlayerOptional = Optional.empty(); //this.quizGameRepository.findGameWithOnePlayer();

        GamePairEntity gamePair;
        if (gamePairWithOnePlayerOptional.isPresent()) {
            gamePair = this.connectToExistingGamePair(userId, gamePairWithOnePlayerOptional.get());
        } else {
            gamePair = this.createNewGamePair(userId);
        }

        return this.quizGamePairRepository.save(gamePair);
    }

    private GamePairEntity createNewGamePair(String userId) {
        UserEntity user = this.userService.findById(UUID.fromString(userId));
        return GamePairEntity.createInstance(user);
    }

    private GamePairEntity connectToExistingGamePair(String userId, GamePairEntity gamePair) {
        this.addSecondUserToExistingGamePair(userId, gamePair);
        this.createFiveRandomQuestionsToCurrentGame(gamePair);
        return gamePair;
    }

    private void createFiveRandomQuestionsToCurrentGame(GamePairEntity gamePair) {
        List<QuizQuestionEntity> quizQuestionEntityList = this.quizQuestionService.getRandomQuestionByQuantity(5);
        gamePair.setQuestions(quizQuestionEntityList);
    }


    private void addSecondUserToExistingGamePair(String userId, GamePairEntity gamePair) {
        UserEntity user = this.userService.findById(UUID.fromString(userId));
        gamePair.getSecondPlayerProgress().setPlayer(user);
        gamePair.setStatus(GamePairStatus.ACTIVE);
    }


    public AnswerEntity answerGameQuestion(String userId, AnswerQuestionDto answerQuestionDto) {
        GamePairEntity currentUserGamePair = this.getCurrentUserGamePair(userId);

        AnswerEntity answeredQuestion = this.answerGameQuestionByUser(currentUserGamePair, userId, answerQuestionDto);

        boolean isAllQuestionsInGameAnswered = this.isAllQuestionsInGameAnswered(currentUserGamePair);
        if (isAllQuestionsInGameAnswered) {
            this.finishGame(currentUserGamePair);
        }


        return answeredQuestion;
    }

    private AnswerEntity answerGameQuestionByUser(GamePairEntity currentUserGamePair, String userId, AnswerQuestionDto answerQuestionDto) {
        UserEntity user = this.userService.findById(UUID.fromString(userId));
        List<QuizQuestionEntity> currentGameQuestions = currentUserGamePair.getQuestions();

        // Find user number
        boolean isCurrentUserIsFirstPlayer = currentUserGamePair.getFirstPlayerProgress().getPlayer().getId().toString().equals(userId);
        boolean isCurrentUserIsSecondPlayer = currentUserGamePair.getSecondPlayerProgress().getPlayer().getId().toString().equals(userId);


        GamePlayerProgressEntity currentGamePlayerProgress = null;
        if (isCurrentUserIsFirstPlayer) {
            currentGamePlayerProgress = currentUserGamePair.getFirstPlayerProgress();
        }

        if (isCurrentUserIsSecondPlayer) {
            currentGamePlayerProgress = currentUserGamePair.getSecondPlayerProgress();
        }

        // Check current game player progress object
        if (currentGamePlayerProgress == null) {
            throw new RuntimeException("Something goes wrong, can not get current user");
        }

        List<AnswerEntity> currentUserAnswerEntities = currentGamePlayerProgress.getAnswers();

        if (currentUserAnswerEntities.size() >= currentGameQuestions.size()) {
            throw new RuntimeException("User answer to all " + currentGameQuestions.size() + " questions");
        }

        int currentQuestionIndex = currentUserAnswerEntities.size();
        QuizQuestionEntity currentQuestion = currentGameQuestions.get(currentQuestionIndex);

        boolean isAnswerCorrect = quizQuestionService.isAnswerToQuestionCorrect(currentQuestion.getId().toString(), answerQuestionDto.answer());
        AnswerStatus answerStatus = isAnswerCorrect ? AnswerStatus.CORRECT : AnswerStatus.INCORRECT;

        AnswerEntity currentQuestionAnswer = new AnswerEntity();
        currentQuestionAnswer.setQuestion(currentQuestion);
        currentQuestionAnswer.setAnswerStatus(answerStatus);
        AnswerEntity savedAnswerEntity = this.answerRepository.save(currentQuestionAnswer);

        currentGamePlayerProgress.getAnswers().add(savedAnswerEntity);
        if (isAnswerCorrect) {
            currentGamePlayerProgress.setScore(currentGamePlayerProgress.getScore() + 1);
        }
        this.gamePlayerProgressRepository.save(currentGamePlayerProgress);

        return savedAnswerEntity;
    }

    private boolean isAllQuestionsInGameAnswered(GamePairEntity gamePair) {
        int gameQuestionsSize = gamePair.getQuestions().size();
        int firstPlayerAnswersSize = gamePair.getFirstPlayerProgress().getAnswers().size();
        int secondPlayerAnswersSize = gamePair.getSecondPlayerProgress().getAnswers().size();

        if (gameQuestionsSize == firstPlayerAnswersSize && gameQuestionsSize == secondPlayerAnswersSize) {
            return true;
        }

        return false;
    }

    private void finishGame(GamePairEntity gamePair) {
        gamePair.setStatus(GamePairStatus.FINISHED);
        this.calculateUserScore(gamePair.getFirstPlayerProgress(), gamePair.getSecondPlayerProgress());
        this.calculateUserScore(gamePair.getSecondPlayerProgress(), gamePair.getFirstPlayerProgress());
    }

    private void calculateUserScore(GamePlayerProgressEntity calculatedUserGameProgress, GamePlayerProgressEntity otherUserGameProgress) {
        int userAnswersScore = (int) this.getCorrectUserAnswersScore(calculatedUserGameProgress); // TODO maybe delete
        int additionalPoints = this.calculateAdditionalUserPoints(calculatedUserGameProgress, otherUserGameProgress);
        calculatedUserGameProgress.setScore(userAnswersScore + additionalPoints);
        this.gamePlayerProgressRepository.save(calculatedUserGameProgress);
    }

    private long getCorrectUserAnswersScore(GamePlayerProgressEntity gamePlayerProgress) {
        return gamePlayerProgress.getAnswers().stream()
                .filter(answerEntity -> answerEntity.getAnswerStatus() == AnswerStatus.CORRECT)
                .count();
    }

    // Calculate only in the end of the game, when all questions have been answered
    private int calculateAdditionalUserPoints(GamePlayerProgressEntity calculatedUserGameProgress, GamePlayerProgressEntity otherUserGameProgress) {
        long correctUserAnswersQuantity = this.getCorrectUserAnswersScore(calculatedUserGameProgress);

        if (correctUserAnswersQuantity == 0) {
            return 0;
        }

        boolean isUserAnsweredAllQuestionsFasterThanOther = true;

        for (int i = 0; i < calculatedUserGameProgress.getAnswers().size(); i++) {
            AnswerEntity currentUserAnswer = calculatedUserGameProgress.getAnswers().get(i);
            AnswerEntity otherUserAnswer = otherUserGameProgress.getAnswers().get(i);

            boolean isAnsweredFasterForCurrentQuestion = currentUserAnswer.getAddedAt().isBefore(otherUserAnswer.getAddedAt());
            if (!isAnsweredFasterForCurrentQuestion) {
                isUserAnsweredAllQuestionsFasterThanOther = false;
                break;
            }
        }

        return isUserAnsweredAllQuestionsFasterThanOther ? 1 : 0;
    }

}
