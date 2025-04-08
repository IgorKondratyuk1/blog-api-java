package org.development.blogApi.modules.quiz.pairQuizGame.entity;

import jakarta.persistence.*;
import lombok.*;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.enums.AnswerStatus;
import org.development.blogApi.modules.quiz.question.entity.QuizQuestionEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "quiz_game_answer")
@EntityListeners(AuditingEntityListener.class)
public class AnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.PACKAGE)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "quiz_question_id")
    private QuizQuestionEntity question;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "answer_status")
    private AnswerStatus answerStatus;

    @CreatedDate
    @Setter(AccessLevel.PACKAGE)
    @Column(name = "added_at", updatable = false)
    private LocalDateTime addedAt;
}
