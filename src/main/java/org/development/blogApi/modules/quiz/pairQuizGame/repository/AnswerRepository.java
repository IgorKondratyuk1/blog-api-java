package org.development.blogApi.modules.quiz.pairQuizGame.repository;

import org.development.blogApi.modules.quiz.pairQuizGame.entity.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<AnswerEntity, UUID> {
}
