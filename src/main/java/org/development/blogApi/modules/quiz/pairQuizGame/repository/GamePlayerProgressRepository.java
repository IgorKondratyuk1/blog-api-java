package org.development.blogApi.modules.quiz.pairQuizGame.repository;

import org.development.blogApi.modules.quiz.pairQuizGame.entity.GamePlayerProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GamePlayerProgressRepository extends JpaRepository<GamePlayerProgressEntity, UUID> {
}
