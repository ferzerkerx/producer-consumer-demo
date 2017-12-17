package com.ferzerkerx.demoplayground.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import java.util.concurrent.BlockingQueue;

import static com.ferzerkerx.demoplayground.demo.Util.waitForThreadIsDone;
import static java.lang.Thread.currentThread;

public class Consumer implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(Producer.class);

    @Nonnull
    private final Thread consumerThread;
    @Nonnull
    private final BlockingQueue<Integer> queue;

    public Consumer(@Nonnull BlockingQueue<Integer> queue) {
        consumerThread = new Thread(this::consume);
        consumerThread.start();
        this.queue = queue;
    }


    public void consume() {
        try {
            while (!currentThread().isInterrupted()) {
                Integer number = queue.take();
                LOG.info("Consume: {}" , number);
            }
        } catch (InterruptedException e) {
            currentThread().interrupt();
        }
    }


    @Override
    public void close() {
        LOG.info("Killing threads....");
        waitForThreadIsDone(consumerThread);
    }
}
