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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

import static com.firstexample.common.Settings.MESSAGES_TO_SEND;

@Slf4j
@SpringBootApplication
public class ProducerApplication implements CommandLineRunner {

    private final long[] numbersToSend = LongStream.rangeClosed(1, MESSAGES_TO_SEND).toArray();

    private final AtomicInteger cnt = new AtomicInteger(0);

    private static final int BYTES_TO_FILL = 842;

    private static final String PADDING = "A".repeat(BYTES_TO_FILL);

    @Value("${send.interval}")
    private int sendInterval;

    @Autowired
    private StreamBridge streamBridge;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("send interval = {} microseconds", this.sendInterval);
        log.info("checksum = {}", LongStream.rangeClosed(1, MESSAGES_TO_SEND).sum());

        log.info("Start sending at {}", Instant.now());
        executorService.scheduleAtFixedRate(sendMessage(), 1_000_000, sendInterval, TimeUnit.MICROSECONDS);
    }


    private Runnable sendMessage() {
        return () -> {
            int currentCnt = cnt.getAndIncrement();
            if (currentCnt >= MESSAGES_TO_SEND) {
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
