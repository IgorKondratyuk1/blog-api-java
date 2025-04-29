package org.development.blogApi.modules.quiz.pairQuizGame.repository;

import org.development.blogApi.modules.quiz.pairQuizGame.entity.GamePairEntity;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.enums.GamePairStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizGamePairRepository extends JpaRepository<GamePairEntity, UUID> {

    @Query("SELECT gp FROM GamePairEntity gp WHERE gp.secondPlayerProgress IS NULL ORDER BY gp.pairCreatedDate ASC")
    Optional<GamePairEntity> findGameWithOnePlayer();

    @Query("SELECT gp FROM GamePairEntity gp " +
            "LEFT JOIN gp.firstPlayerProgress fp LEFT JOIN fp.player fpPlayer " +
            "LEFT JOIN gp.secondPlayerProgress sp LEFT JOIN sp.player spPlayer " +
            "WHERE gp.status IN :gamePairStatusList " +
            "AND (fpPlayer.id = :userId OR spPlayer.id = :userId)")
    Optional<GamePairEntity> findGameByUserIdAndGameStatus(UUID userId, List<GamePairStatus> gamePairStatusList);

    @Query("SELECT gp FROM GamePairEntity gp " +
            "LEFT JOIN gp.firstPlayerProgress fp LEFT JOIN fp.player fpPlayer " +
            "LEFT JOIN gp.secondPlayerProgress sp LEFT JOIN sp.player spPlayer " +
            "WHERE (fpPlayer.id = :userId OR spPlayer.id = :userId)")
    List<GamePairEntity> findAllGameByUserId(UUID userId);

    @Query("SELECT gp FROM GamePairEntity gp " +
            "LEFT JOIN gp.firstPlayerProgress fp LEFT JOIN fp.player fpPlayer " +
            "LEFT JOIN gp.secondPlayerProgress sp LEFT JOIN sp.player spPlayer " +
            "WHERE gp.id = :gamePairId " +
            "AND (fpPlayer.id = :userId OR spPlayer.id = :userId)")
    Optional<GamePairEntity> findGameByUserIdAndGameId(UUID userId, UUID gamePairId);
}
