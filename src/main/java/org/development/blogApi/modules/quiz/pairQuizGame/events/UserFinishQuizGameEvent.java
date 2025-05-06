package org.development.blogApi.modules.quiz.pairQuizGame.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
@Setter
public class UserFinishQuizGameEvent extends ApplicationEvent {

    private final UUID gamePairId;

    public UserFinishQuizGameEvent(Object source, UUID gamePairId) {
        super(source);
        this.gamePairId = gamePairId;
    }
}
