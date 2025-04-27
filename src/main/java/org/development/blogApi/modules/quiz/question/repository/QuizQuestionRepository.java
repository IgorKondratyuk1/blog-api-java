package org.development.blogApi.modules.quiz.question.repository;

import org.development.blogApi.modules.quiz.question.entity.QuizQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestionEntity, UUID> {

    @Query(value = "SELECT * FROM (SELECT * FROM quiz_question t WHERE t.published = TRUE ORDER BY RANDOM() LIMIT :quantity) ot ORDER BY ot.created_at", nativeQuery = true)
    List<QuizQuestionEntity> findRandomQuestionsByQuantity(int quantity);
}
