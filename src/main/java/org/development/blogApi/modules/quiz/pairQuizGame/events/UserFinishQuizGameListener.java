package org.development.blogApi.modules.quiz.pairQuizGame.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.development.blogApi.modules.quiz.pairQuizGame.service.QuizGamePairService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserFinishQuizGameListener {

    private final QuizGamePairService quizGamePairService;

    @EventListener
    public void handleDelayedEvent(UserFinishQuizGameEvent event) {
        System.out.println("Handled UserFinishQuizGameEvent\n" + event);
        try {
            this.quizGamePairService.finishGame(event.getGamePairId().toString());
        } catch (Exception e) {
            log.error("HandleDelayedEvent Error", e);
        }
    }
}
