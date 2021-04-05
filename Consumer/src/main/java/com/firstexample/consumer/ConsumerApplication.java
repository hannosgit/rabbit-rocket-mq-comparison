package com.firstexample.consumer;

import com.firstexample.common.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class ConsumerApplication implements CommandLineRunner {
    private final ConcurrentLinkedQueue<Person> receivedTestObjects = new ConcurrentLinkedQueue<>();
    private final AtomicLong receivedTestObjectsCount = new AtomicLong(0);

    private long checkReceivedObjectsCount;

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Bean
    public Consumer<Person> receiveTestObject() {
        return person -> {
            if (person.getMessageNumber() == -1L) {
                // ignore warmup messages
                return;
            }
            receivedTestObjectsCount.incrementAndGet();

            person.setReceiveTimestamp(System.currentTimeMillis());
            receivedTestObjects.add(person);
        };
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) {
    }

    private void executeAfterAllReceived() {
        String resultFileName = String.format("./results/results_%s.csv", Instant.now());

        try {
            Thread.sleep(1000);
            ResultWriter.writeResult(resultFileName, receivedTestObjects);
            log.info("writing result to CSV '{}' done", resultFileName);
            receivedTestObjects.clear();
            receivedTestObjectsCount.set(0);
            log.info("Clearing results done");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedDelay = 10_000L)
    private void log() {
        long currentCount = receivedTestObjectsCount.get();
        log.info("Objects received: {}", currentCount);

        if (this.checkReceivedObjectsCount != 0 && currentCount == this.checkReceivedObjectsCount) {
            log.info("No Messages received since 10 seconds -> write result!");
            executeAfterAllReceived();
        }

        this.checkReceivedObjectsCount = currentCount;

    }

}
