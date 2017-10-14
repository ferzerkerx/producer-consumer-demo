package com.ferzerkerx.demoplayground.demo;

import javax.annotation.Nonnull;

import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.SECONDS;

public final class Util {

    private Util() {
    }

    public static void waitForThreadIsDone(@Nonnull Thread thread) {
        boolean wasInterrupted = false;
        try {
            while (thread.isAlive()) {
                thread.interrupt();
                try {
                    thread.join(SECONDS.toMillis(10));
                } catch (InterruptedException ignored) {
                    ignored.printStackTrace();
                    wasInterrupted = true;
                }
            }
        } finally {
            if (wasInterrupted) {
                currentThread().interrupt();
            }
        }
    }


}
