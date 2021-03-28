package com.firstexample.consumer;

import com.firstexample.common.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static com.firstexample.common.Settings.MESSAGES_TO_SEND;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class ConsumerApplication implements CommandLineRunner {

    private static final String RESULT_FILE_NAME = "result.csv";

    private final ConcurrentLinkedQueue<Person> receivedTestObjects = new ConcurrentLinkedQueue<>();
    private final AtomicLong receivedTestObjectsCount = new AtomicLong(0);


    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Bean
    public Consumer<Person> receiveTestObject() {
        return person -> {
            final long currentCount = receivedTestObjectsCount.incrementAndGet();

            person.setReceiveTimestamp(System.currentTimeMillis());
            receivedTestObjects.add(person);

            if (currentCount == MESSAGES_TO_SEND) {
                System.out.printf("Received %s messages%n", MESSAGES_TO_SEND);
                executeAfterAllReceived();
            }
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
        try {
            Thread.sleep(1000);
            ResultWriter.writeResult(RESULT_FILE_NAME, receivedTestObjects);
            System.out.printf("writing result to CSV '%s' done%n", RESULT_FILE_NAME);

            receivedTestObjects.clear();
            receivedTestObjectsCount.set(0);
            System.out.println("Clearing results done");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
