package org.development.blogApi.modules.quiz.question.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "quiz_question")
@EntityListeners(AuditingEntityListener.class)
public class QuizQuestionEntity {

    @Id
    @Setter(AccessLevel.PACKAGE)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "body", nullable = false, length = 1000)
    private String body;

    @ElementCollection
    @CollectionTable(name = "quiz_question_correct_answers", joinColumns = @JoinColumn(name = "question_id"))
    private List<String> correctAnswers;

    @Builder.Default
    @Column(name = "published", nullable = false)
    private Boolean published = false;

    @CreatedDate
    @Setter(AccessLevel.PACKAGE)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Setter(AccessLevel.PACKAGE)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    private void onPreUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
