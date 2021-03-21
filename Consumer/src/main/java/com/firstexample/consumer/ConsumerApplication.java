package com.firstexample.consumer;

import com.firstexample.common.TestObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static com.firstexample.common.Settings.MESSAGES_TO_SEND;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class ConsumerApplication implements CommandLineRunner {

    private static final String RESULT_FILE_NAME = "result.csv";

    private final ConcurrentLinkedQueue<TestObject> receivedTestObjects = new ConcurrentLinkedQueue<>();
    private final AtomicLong receivedTestObjectsCount = new AtomicLong(0);


    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Bean
    public Consumer<TestObject> receiveTestObject() {
        return testObject -> {
            testObject.setReceiveTimestamp(System.currentTimeMillis());
            receivedTestObjects.add(testObject);
            if (receivedTestObjectsCount.incrementAndGet() == MESSAGES_TO_SEND) {
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

//    @Scheduled(fixedDelay = 5000L)
//    public void bla(){
//        System.out.println(receivedTestObjectsCount.get());
//        System.out.println(receivedTestObjects.size());
//    }

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
