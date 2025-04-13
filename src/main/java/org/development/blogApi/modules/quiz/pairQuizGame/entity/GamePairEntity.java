package org.development.blogApi.modules.quiz.pairQuizGame.entity;

import jakarta.persistence.*;
import lombok.*;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.enums.GamePairStatus;
import org.development.blogApi.modules.quiz.question.entity.QuizQuestionEntity;
import org.development.blogApi.modules.user.entity.UserEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "quiz_game_pair")
@EntityListeners(AuditingEntityListener.class)
public class GamePairEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.PACKAGE)
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "first_player_id")
    private GamePlayerProgressEntity firstPlayerProgress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "second_player_id")
    private GamePlayerProgressEntity secondPlayerProgress;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
    @JoinTable(
            name = "quiz_game_pair_questions",
            joinColumns = @JoinColumn(name = "game_pair_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<QuizQuestionEntity> questions;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private GamePairStatus status = GamePairStatus.PENDING;

    @CreatedDate
    @Setter(AccessLevel.PACKAGE)
    @Column(nullable = false, updatable = false)
    private LocalDateTime pairCreatedDate;

    @Column(name = "start_game_date")
    private LocalDateTime startGameDate;

    @Column(name = "finish_game_date")
    private LocalDateTime finishGameDate;

    @Version
    @Column(name = "version")
    private Integer version;

    public static GamePairEntity createInstance(UserEntity user) {
        GamePairEntity gamePairEntity = new GamePairEntity();
        gamePairEntity.setStatus(GamePairStatus.PENDING);

        GamePlayerProgressEntity firstPlayerProgressEntity = GamePlayerProgressEntity.createInstance(user);
        gamePairEntity.setFirstPlayerProgress(firstPlayerProgressEntity);

        return gamePairEntity;
    }
}
