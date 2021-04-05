package com.firstexample.producer;

import com.firstexample.common.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;

import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

import static com.firstexample.common.Settings.MESSAGES_TO_SEND;
import static com.firstexample.common.Settings.WARM_UP_MESSAGE_COUNT;

@Slf4j
@SpringBootApplication
public class ProducerApplication implements CommandLineRunner {

    private static final int ATTEMPTS = 6;

    private static final long WAIT_BETWEEN_ATTEMPTS = 120_000L;

    private final long[] numbersToSend = LongStream.concat(
            LongStream.generate(() -> -1L).limit(WARM_UP_MESSAGE_COUNT),
            LongStream.rangeClosed(1, MESSAGES_TO_SEND))
            .toArray();

    private final AtomicInteger cnt = new AtomicInteger(0);

    private static final int BYTES_TO_FILL = 842;

    private static final String PADDING = "A".repeat(BYTES_TO_FILL);

    private ScheduledExecutorService executorService;

    @Value("${send.interval}")
    private int sendInterval;

    @Autowired
    private StreamBridge streamBridge;


    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        for (int i = 1; i <= ATTEMPTS; i++) {
            // Set up
            executorService = Executors.newSingleThreadScheduledExecutor();
            cnt.set(0);

            log.info("---------------------- ATTEMPT {} ----------------------", i);
            log.info("send interval = {} microseconds", this.sendInterval);
            log.info("checksum = {}", LongStream.rangeClosed(1, MESSAGES_TO_SEND).sum());

            log.info("Start sending at {}", Instant.now());
            ScheduledFuture<?> sendFuture =executorService.scheduleAtFixedRate(sendMessage(), 1_000_000, sendInterval, TimeUnit.MICROSECONDS);
            try {
                sendFuture.get();
            } catch (IllegalStateException | InterruptedException | ExecutionException e) {
                //e.printStackTrace();
            }
            try {
                log.info("Wait for 2 min before next attempt!");
                Thread.sleep(WAIT_BETWEEN_ATTEMPTS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }


    private Runnable sendMessage() {
        return () -> {
            int currentCnt = cnt.getAndIncrement();
            if (currentCnt >= MESSAGES_TO_SEND + WARM_UP_MESSAGE_COUNT) {
                log.info("sending done at {}! cnt: {}, arraysize: {}", Instant.now(), cnt.get(), numbersToSend.length);
                executorService.shutdown();
                return;
            }
            Person person = new Person(1, "John", "Smith", "New York", PADDING, numbersToSend[currentCnt], 0, 0);
            person.setSendTimestamp(System.currentTimeMillis());
            streamBridge.send("sendtestobject-out-0", person);
        };

    }

}
