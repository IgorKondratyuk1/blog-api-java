package org.development.blogApi.modules.quiz.question.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "quiz_question")
public class QuizQuestionEntity {

    @Id
    @Setter(AccessLevel.PACKAGE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "body", nullable = false)
    private String body;

    @ElementCollection
    @CollectionTable(name = "quiz_question_correct_answers", joinColumns = @JoinColumn(name = "question_id"))
    private List<String> correctAnswers;

    @Builder.Default
    @Column(name = "published", nullable = false)
    private Boolean published = false;

    @CreationTimestamp
    @Setter(AccessLevel.PACKAGE)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Setter(AccessLevel.PACKAGE)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
