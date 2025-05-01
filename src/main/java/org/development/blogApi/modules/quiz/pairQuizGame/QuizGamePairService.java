package org.development.blogApi.modules.quiz.pairQuizGame;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.common.utils.PaginationUtil;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.TopUsersQueryParams;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.request.AnswerQuestionDto;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.response.ViewMyStatisticDto;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.response.ViewPlayerDto;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.response.ViewUserStatisticDto;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.AnswerEntity;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.GamePairEntity;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.GamePlayerProgressEntity;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.enums.AnswerStatus;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.enums.GamePairStatus;
import org.development.blogApi.modules.quiz.pairQuizGame.exceptions.*;
import org.development.blogApi.modules.quiz.pairQuizGame.repository.*;
import org.development.blogApi.modules.quiz.pairQuizGame.utils.SortStatisticUtil;
import org.development.blogApi.modules.quiz.question.QuizQuestionService;
import org.development.blogApi.modules.quiz.question.entity.QuizQuestionEntity;
import org.development.blogApi.modules.user.UserService;
import org.development.blogApi.modules.user.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class QuizGamePairService {

    @PersistenceContext
    private EntityManager entityManager;

    private final QuizGamePairRepository quizGamePairRepository;

    private final QuizQuestionService quizQuestionService;

    private final GamePlayerProgressRepository gamePlayerProgressRepository;

    private final AnswerRepository answerRepository;

    private final UserService userService;

    public Optional<GamePairEntity> getCurrentUserGamePair(String userId) {
        return this.quizGamePairRepository.findGameByUserIdAndGameStatus(UUID.fromString(userId), List.of(GamePairStatus.ACTIVE, GamePairStatus.PENDING));
    }

    public GamePairEntity getGamePairByIdAndParticipantUser(String userId, String gamePairId) {
        GamePairEntity foundedGamePair = this.quizGamePairRepository.findById(UUID.fromString(gamePairId))
                .orElseThrow(() -> new GamePairNotFoundException());

        Optional<GamePairEntity> gamePairEntityByUser = this.quizGamePairRepository.findGameByUserIdAndGameId(UUID.fromString(userId), UUID.fromString(gamePairId));
        boolean isUserParticipateInGame = gamePairEntityByUser.isPresent();

        // If current user not participate in game pair
        if (!isUserParticipateInGame) {
            throw new GamePairForbiddenException("GamePair forbidden to not owning user");
        }

        return foundedGamePair;
    }

    public PaginationDto<ViewUserStatisticDto> getTopUsersStatistic(TopUsersQueryParams topUsersQueryParams) {
        List<ViewUserStatisticDto> allUsersStatisticDtos = new ArrayList<>();
        List<UUID> uniqueAllPlayerIds = this.gamePlayerProgressRepository.findAllUniquePlayerIds();

        uniqueAllPlayerIds.forEach(id -> {
            ViewMyStatisticDto userStatistic = this.getCurrentUserStatistic(id.toString());
            UserEntity user = this.userService.findById(id);
            ViewPlayerDto playerDto = new ViewPlayerDto(user.getId().toString(), user.getLogin());

            allUsersStatisticDtos.add(new ViewUserStatisticDto(
                    userStatistic.sumScore(),
                    userStatistic.avgScores(),
                    userStatistic.gamesCount(),
                    userStatistic.winsCount(),
                    userStatistic.lossesCount(),
                    userStatistic.drawsCount(),
                    playerDto
            ));
        });

        SortStatisticUtil.sortListByFields(allUsersStatisticDtos, topUsersQueryParams.getSortBy());

        int skipValue = PaginationUtil.getSkipValue(topUsersQueryParams.getPageNumber(), topUsersQueryParams.getPageSize());
        System.out.println("skipValue " + skipValue);
        Long totalCount = Long.valueOf(uniqueAllPlayerIds.size());
        int pagesCount = PaginationUtil.getPagesCount(totalCount, topUsersQueryParams.getPageSize());

        List<ViewUserStatisticDto> usersStatisticDtosPage = new ArrayList<>();
        if (allUsersStatisticDtos.size() > skipValue) {
            int lastPageElementIndex = Math.min(allUsersStatisticDtos.size(), skipValue + topUsersQueryParams.getPageSize());
            System.out.println("lastPageElementIndex " + lastPageElementIndex);
            usersStatisticDtosPage = allUsersStatisticDtos.subList(skipValue, lastPageElementIndex);
        }

        return new PaginationDto<>(pagesCount,
                topUsersQueryParams.getPageNumber(),
                topUsersQueryParams.getPageSize(),
                totalCount,
                usersStatisticDtosPage);
    }

    public ViewMyStatisticDto getCurrentUserStatistic(String userId) {
        List<GamePairEntity> gamePairEntities = this.quizGamePairRepository.findAllGameByUserId(UUID.fromString(userId));

        int sumScore = this.gamePlayerProgressRepository.getSumScoreOfAllPlayerGames(UUID.fromString(userId));
        double avgScores = this.gamePlayerProgressRepository.getAvgScoreOfAllPlayerGames(UUID.fromString(userId));
        int gamesCount = this.gamePlayerProgressRepository.getGamesCountOfAllPlayerGames(UUID.fromString(userId));

        int winsCount = this.getAllWinsCountByUserId(gamePairEntities, userId);
        int lossesCount = this.getAllLossesCountByUserId(gamePairEntities, userId);
        int drawsCount = this.getAllDrawsCountByUserId(gamePairEntities, userId);

        return new ViewMyStatisticDto(
                sumScore,
                avgScores,
                gamesCount,
                winsCount,
                lossesCount,
                drawsCount
        );
    }

    private int getAllWinsCountByUserId(List<GamePairEntity> gamePairEntities, String userId) {
        UUID userUUID = UUID.fromString(userId);

        return (int) gamePairEntities
                .stream()
                .filter(
                        gamePair -> {
                            var first = gamePair.getFirstPlayerProgress();
                            var second = gamePair.getSecondPlayerProgress();

                            boolean isUserFirst = first.getPlayer().getId().equals(userUUID);
                            int userScore = isUserFirst ? first.getScore() : second.getScore();
                            int opponentScore = isUserFirst ? second.getScore() : first.getScore();

                            return userScore > opponentScore;
                        }
                )
                .count();
    }

    private int getAllLossesCountByUserId(List<GamePairEntity> gamePairEntities, String userId) {
        UUID userUUID = UUID.fromString(userId);

        return (int) gamePairEntities
                .stream()
                .filter(
                        gamePair -> {
                            var first = gamePair.getFirstPlayerProgress();
                            var second = gamePair.getSecondPlayerProgress();

                            boolean isUserFirst = first.getPlayer().getId().equals(userUUID);
                            int userScore = isUserFirst ? first.getScore() : second.getScore();
                            int opponentScore = isUserFirst ? second.getScore() : first.getScore();

                            return userScore < opponentScore;
                        }
                )
                .count();
    }

    private int getAllDrawsCountByUserId(List<GamePairEntity> gamePairEntities, String userId) {
        UUID userUUID = UUID.fromString(userId);

        return (int) gamePairEntities
                .stream()
                .filter(
                        gamePair -> {
                            var first = gamePair.getFirstPlayerProgress();
                            var second = gamePair.getSecondPlayerProgress();

                            return first.getScore() == second.getScore();
                        }
                )
                .count();
    }

    public GamePairEntity connectUserToGamePair(String userId) {
        UserEntity user = this.userService.findById(UUID.fromString(userId));
        Optional<GamePairEntity> activeGamePair = this.quizGamePairRepository.findGameByUserIdAndGameStatus(user.getId(), List.of(GamePairStatus.ACTIVE, GamePairStatus.PENDING));

        if (activeGamePair.isPresent()) {
            throw new UserAlreadyHasGamePairException(activeGamePair.get().getId().toString());
        }

        // TODO check
        Optional<GamePairEntity> gamePairWithOnePlayerOptional = this.quizGamePairRepository.findGameWithOnePlayer();

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
        GamePairEntity gamePair = GamePairEntity.createInstance(user);
        return gamePair;
    }

    private GamePairEntity connectToExistingGamePair(String userId, GamePairEntity gamePair) {
        this.addSecondUserToExistingGamePair(userId, gamePair);
        this.createFiveRandomQuestionsToCurrentGame(gamePair);
        this.setAdditionalInfoForGameStart(gamePair);

        return gamePair;
    }

    private void createFiveRandomQuestionsToCurrentGame(GamePairEntity gamePair) {
        List<QuizQuestionEntity> quizQuestionEntityList = this.quizQuestionService.getRandomQuestionByQuantity(5);
        gamePair.setQuestions(quizQuestionEntityList);
    }

    private void addSecondUserToExistingGamePair(String userId, GamePairEntity gamePair) {
        UserEntity user = this.userService.findById(UUID.fromString(userId));
        GamePlayerProgressEntity secondPlayerProgress = GamePlayerProgressEntity.createInstance(user);
        gamePair.setSecondPlayerProgress(secondPlayerProgress);
    }

    private void setAdditionalInfoForGameStart(GamePairEntity gamePair) {
        gamePair.setStartGameDate(LocalDateTime.now());
        gamePair.setStatus(GamePairStatus.ACTIVE);
    }

    public AnswerEntity answerGameQuestion(String userId, AnswerQuestionDto answerQuestionDto) {
        GamePairEntity currentUserGamePair = this.getCurrentUserGamePair(userId)
                .orElseThrow(() -> new GamePairForbiddenException("Forbidden to answer for question with no game pair"));

        if (currentUserGamePair.getStatus() != GamePairStatus.ACTIVE) {
            throw new WrongStateOfGamePairException("Can not answer the question, because game status is not \"Active\", it's " + currentUserGamePair.getStatus());
        }

        AnswerEntity answeredQuestion = this.answerGameQuestionByUser(currentUserGamePair, userId, answerQuestionDto);

        boolean isAllQuestionsInGameAnswered = this.isAllQuestionsInGameAnswered(currentUserGamePair);
        if (isAllQuestionsInGameAnswered) {
            this.finishGame(currentUserGamePair.getId().toString());
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
            throw new NoAvailableQuestionsException();
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

        System.out.println("need save");
        entityManager.flush();

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

    private void finishGame(String gamePairId) {
        GamePairEntity gamePair = this.quizGamePairRepository.findById(UUID.fromString(gamePairId))
                .orElseThrow(() -> new GamePairNotFoundException());

        this.calculateUserScore(gamePair.getFirstPlayerProgress(), gamePair.getSecondPlayerProgress());
        this.calculateUserScore(gamePair.getSecondPlayerProgress(), gamePair.getFirstPlayerProgress());
        this.setAdditionalInfoForGameFinish(gamePair);
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

    // Calculate only in the end of the game. Answered first for last question
    private int calculateAdditionalUserPoints(GamePlayerProgressEntity calculatedUserGameProgress, GamePlayerProgressEntity otherUserGameProgress) {
        long correctUserAnswersQuantity = this.getCorrectUserAnswersScore(calculatedUserGameProgress);

        if (correctUserAnswersQuantity == 0) {
            return 0;
        }

        AnswerEntity currentUserAnswer = calculatedUserGameProgress.getAnswers().get(calculatedUserGameProgress.getAnswers().size() - 1);
        AnswerEntity otherUserAnswer = otherUserGameProgress.getAnswers().get(calculatedUserGameProgress.getAnswers().size() - 1);
        boolean isUserAnsweredAllQuestionsFasterThanOther = currentUserAnswer.getAddedAt().isBefore(otherUserAnswer.getAddedAt());

        return isUserAnsweredAllQuestionsFasterThanOther ? 1 : 0;
    }

    private void setAdditionalInfoForGameFinish(GamePairEntity gamePair) {
        gamePair.setStatus(GamePairStatus.FINISHED);
        gamePair.setFinishGameDate(LocalDateTime.now());
    }
}
