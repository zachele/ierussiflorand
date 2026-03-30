package com.example.shopflowers.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class CartTimerManager {

    private static final int TIMEOUT_SECONDS = 600; // 10 minuti
    private static Timeline timeline;
    private static Runnable onTimeoutAction;
    private static java.util.function.Consumer<Integer> onTickAction;
    private static int remainingSeconds = TIMEOUT_SECONDS;

    private CartTimerManager() {
    }

    public static void setOnTimeoutAction(Runnable action) {
        onTimeoutAction = action;
    }

    public static void setOnTickAction(java.util.function.Consumer<Integer> action) {
        onTickAction = action;
    }

    public static void startOrResetTimer() {
        stopTimerInternal();
        remainingSeconds = TIMEOUT_SECONDS;
        notifyTick();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            remainingSeconds--;
            notifyTick();

            if (remainingSeconds <= 0) {
                stopTimerInternal();
                if (onTimeoutAction != null) {
                    onTimeoutAction.run();
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.playFromStart();
    }

    public static void stopTimer() {
        stopTimerInternal();
        remainingSeconds = TIMEOUT_SECONDS;
        notifyTick();
    }

    public static boolean isRunning() {
        return timeline != null;
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