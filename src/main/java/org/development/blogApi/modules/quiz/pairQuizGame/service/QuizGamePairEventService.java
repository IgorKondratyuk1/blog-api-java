package org.development.blogApi.modules.quiz.pairQuizGame.service;

import lombok.RequiredArgsConstructor;
import org.development.blogApi.modules.quiz.pairQuizGame.events.UserFinishQuizGameEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class QuizGamePairEventService {

    private final ApplicationEventPublisher publisher;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public void triggerUserFinishQuizGame(UUID gamePairId) {
        System.out.println("Trigger UserFinishQuizGame gamePairId: " + gamePairId);
        UserFinishQuizGameEvent event = new UserFinishQuizGameEvent(this, gamePairId);
        scheduledExecutorService.schedule(() -> publisher.publishEvent(event), 10, TimeUnit.SECONDS);
    }
}
