package com.firstexample.producer;

import com.firstexample.common.TestObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.firstexample.common.Settings.MESSAGES_TO_SEND;

@Slf4j
@SpringBootApplication
public class ProducerApplication implements CommandLineRunner {

    private final int[] numbersToSend = IntStream.rangeClosed(1, MESSAGES_TO_SEND).toArray();

    private final AtomicInteger cnt = new AtomicInteger(0);

    @Autowired
    private StreamBridge streamBridge;

    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(100);

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("checksum = {}", Arrays.stream(numbersToSend).sum());

        streamBridge.send("test-out-0", "123");
        log.info("start sending, {}", Instant.now());

        executorService.scheduleAtFixedRate(sendMessage(),1_000_000,100,TimeUnit.MICROSECONDS);
    }


    private Runnable sendMessage() {
        return () -> {
            int currentCnt = cnt.getAndIncrement();
            if(currentCnt >= MESSAGES_TO_SEND){
                log.info("sending done! cnt: {}, arraysize: {}",cnt.get(),numbersToSend.length);
                executorService.shutdown();
                return;
            }
            TestObject testObject = new TestObject("john", numbersToSend[currentCnt], 0, 0);
            testObject.setSendTimestamp(System.currentTimeMillis());
            streamBridge.send("sendtestobject-out-0", testObject);
        };

    }

}
