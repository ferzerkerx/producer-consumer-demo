package com.ferzerkerx.demoplayground.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(DemoApplication.class);
        try (final ConfigurableApplicationContext ignored = app.run(args)) {

        } catch (final Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            //noinspection CallToSystemExit
            System.exit(1);
        }
    }


    private final BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(1000);

    private boolean isMultipleOfTwo(Integer integer) {
        return integer % 2 == 0;
    }

    private boolean isMultipleOfThree(Integer integer) {
        return integer % 3 == 0;
    }


    @Override
    public void run(String... strings) throws Exception {
        LOG.info("Started");
        try(Producer producer = new Producer(queue)) {
            try (Stream<Integer> numbers = producer.stream()) {
                List<Integer> pickedNumbers =
                        numbers.filter(this::isMultipleOfThree)
                                .filter(this::isMultipleOfTwo)
                                .limit(50_000)
                                .collect(Collectors.toList());
                LOG.info("Picked Numbers: {} {}", pickedNumbers.size() , pickedNumbers);
            }
        }

    }
}
