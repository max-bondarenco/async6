package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@EnableAsync
@SpringBootApplication
@EnableScheduling
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Component
    static
    class TaskScheduler {

        private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        public TaskScheduler() {
            performTaskWithRetries();
        }

        @Async
        private void performTaskWithRetries() {
            Runnable task = new Runnable() {
                private int attempt = 1;

                @Override
                public void run() {
                    boolean success = Math.random() < 0.7;
                    System.out.printf("Attempt %d: %s\n", attempt, success);
                    if (success) {
                        executorService.shutdown();
                    }
                    else if (attempt++ >= 3) {
                        System.err.println("Could not finish in 3 attempts");
                        executorService.shutdown();
                    }
                }
            };
            executorService.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);
        }

        @Async
        @Scheduled(initialDelay = 15000, fixedRate = Long.MAX_VALUE)
        public void printAfter15Seconds() {
            System.out.println("15 seconds after program start");
        }
    }
}

