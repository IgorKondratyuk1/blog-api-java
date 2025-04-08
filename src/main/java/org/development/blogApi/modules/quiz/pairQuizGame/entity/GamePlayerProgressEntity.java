package org.development.blogApi.modules.quiz.pairQuizGame.entity;

import jakarta.persistence.*;
import lombok.*;
import org.development.blogApi.modules.user.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "quiz_game_player_progress")
public class GamePlayerProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.PACKAGE)
    private UUID id;

    @OneToMany
    @JoinColumn(name = "game_player_progress_id")
    private List<AnswerEntity> answers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity player;

    @Column(name = "score")
    private int score = 0;

    @Version
    @Column(name = "version")
    private Integer version;

    public static GamePlayerProgressEntity createInstance(UserEntity user) {
        GamePlayerProgressEntity playerProgressEntity = new GamePlayerProgressEntity();
        playerProgressEntity.setPlayer(user);
        playerProgressEntity.setScore(0);
        return playerProgressEntity;
    }
}
