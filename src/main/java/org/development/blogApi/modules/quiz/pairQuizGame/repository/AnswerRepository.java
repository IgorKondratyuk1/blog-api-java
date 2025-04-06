package org.development.blogApi.modules.quiz.pairQuizGame.repository;

import org.development.blogApi.modules.quiz.pairQuizGame.entity.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnswerRepository extends JpaRepository<AnswerEntity, UUID> {
}
