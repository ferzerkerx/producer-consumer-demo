package com.ferzerkerx.demoplayground.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.ferzerkerx.demoplayground.demo.Util.waitForThreadIsDone;
import static java.lang.Thread.currentThread;

public class Producer implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(Producer.class);

    @Nonnull
    private final BlockingQueue<Integer> queue;

    @Nonnull
    private Thread producerThread;

    public Producer(@Nonnull BlockingQueue<Integer> queue) {
        this.queue = queue;
        producerThread = new Thread(this::produce);
        producerThread.start();

    }

    public void produce() {
        try {
            while (!currentThread().isInterrupted()) {
                Integer number = (int) (Math.random() * 3000);
                boolean offer = queue.offer(number);
                if (!offer) {
                    LOG.warn("could not offer to queue {}", number);
                }
            }
        } catch (Exception e) {
            currentThread().interrupt();
        }
    }


    @Nonnull
    public Stream<Integer> stream() {
        Stream<Integer> stream = StreamSupport.stream(new Spliterator<Integer>() {
            @Override
            public boolean tryAdvance(Consumer<? super Integer> action) {
                try {
                    Integer number = queue.take();
                    action.accept(number);
                    return true;
                } catch (InterruptedException ignore) {
                    currentThread().interrupt();
                    return false;
                }
            }

            @Override
            public Spliterator<Integer> trySplit() {
                return null;
            }

            @Override
            public long estimateSize() {
                return 0;
            }

            @Override
            public int characteristics() {
                return 0;
            }
        }, false);
        return stream.onClose(() -> {
            try {
                close();
            } catch (Exception ignored) {
                LOG.error("Failed to close stream", ignored);
            }
        });
    }

    @Override
    public void close() throws Exception {
        LOG.info("Killing threads....");
        waitForThreadIsDone(producerThread);
    }
}
