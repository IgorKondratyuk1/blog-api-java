package org.development.blogApi.quiz.question.repository;

import org.development.blogApi.quiz.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
}
