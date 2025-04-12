package org.development.blogApi.modules.quiz.question.repository;

import org.development.blogApi.modules.quiz.question.entity.QuizQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuizQuestionQueryRepository extends JpaRepository<QuizQuestionEntity, UUID>, QuizQuestionQueryRepositoryCustom {
}
