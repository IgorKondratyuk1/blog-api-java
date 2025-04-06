package org.development.blogApi.modules.quiz.pairQuizGame.repository;

import org.development.blogApi.modules.quiz.pairQuizGame.entity.GamePairEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface QuizGamePairRepository extends JpaRepository<GamePairEntity, UUID> {

    @Query("SELECT gp FROM GamePairEntity WHERE gp.secondPlayerProgress.player IS NULL ORDER BY gp.pairCreatedDate ASC LIMIT 1")
    Optional<GamePairEntity> findGameWithOnePlayer();

    @Query("SELECT gp FROM GamePairEntity WHERE gp.firstPlayerProgress.player.id = :userId OR gp.secondPlayerProgress.player.id = :userId")
    Optional<GamePairEntity> findGameByUserId(UUID userId);
}
