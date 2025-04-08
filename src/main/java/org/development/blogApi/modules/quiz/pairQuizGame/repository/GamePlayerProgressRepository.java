package org.development.blogApi.modules.quiz.pairQuizGame.repository;

import org.development.blogApi.modules.quiz.pairQuizGame.entity.GamePlayerProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GamePlayerProgressRepository extends JpaRepository<GamePlayerProgressEntity, UUID> {
}
