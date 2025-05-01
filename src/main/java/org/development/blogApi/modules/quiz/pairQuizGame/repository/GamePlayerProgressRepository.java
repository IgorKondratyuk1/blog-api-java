package org.development.blogApi.modules.quiz.pairQuizGame.repository;

import org.development.blogApi.modules.quiz.pairQuizGame.entity.GamePlayerProgressEntity;
import org.development.blogApi.modules.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GamePlayerProgressRepository extends JpaRepository<GamePlayerProgressEntity, UUID> {
    @Query("SELECT SUM(gp.score) FROM GamePlayerProgressEntity gp WHERE gp.player.id = :userId")
    int getSumScoreOfAllPlayerGames(UUID userId);

    @Query("SELECT AVG(gp.score) FROM GamePlayerProgressEntity gp WHERE gp.player.id = :userId")
    double getAvgScoreOfAllPlayerGames(UUID userId);

    @Query("SELECT COUNT(gp.score) FROM GamePlayerProgressEntity gp WHERE gp.player.id = :userId")
    int getGamesCountOfAllPlayerGames(UUID userId);

    @Query("SELECT MAX(gp.player.id) FROM GamePlayerProgressEntity gp GROUP BY gp.player.id")
    List<UUID> findAllUniquePlayerIds();
}
