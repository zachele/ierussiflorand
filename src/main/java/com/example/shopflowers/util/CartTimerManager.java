package com.example.shopflowers.util;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.function.Consumer;

public final class CartTimerManager {

    private static final int TIMEOUT_SECONDS = 600;
    private static Timeline timeline;
    private static Runnable onTimeoutAction;
    private static Consumer<Integer> onTickAction;
    private static int remainingSeconds = TIMEOUT_SECONDS;

    private CartTimerManager() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void setOnTimeoutAction(Runnable action) {
        onTimeoutAction = action;
    }

    public static void setOnTickAction(Consumer<Integer> action) {
        onTickAction = action;
    }

    public static void startOrResetTimer() {
        stopTimerInternal();
        remainingSeconds = TIMEOUT_SECONDS;
        notifyTick();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> handleTimerTick()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
    }

    public static void stopTimer() {
        stopTimerInternal();
        remainingSeconds = TIMEOUT_SECONDS;
        notifyTick();
    }

    private static void handleTimerTick() {
        remainingSeconds--;
        notifyTick();

        if (remainingSeconds <= 0) {
            stopTimerInternal();

            if (onTimeoutAction != null) {
                onTimeoutAction.run();
            }
        }
    }

    private static void stopTimerInternal() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    private static void notifyTick() {
        if (onTickAction != null) {
            onTickAction.accept(remainingSeconds);
        }
    }
}