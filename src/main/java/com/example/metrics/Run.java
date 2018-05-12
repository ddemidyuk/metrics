package com.example.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class Run {

    @Autowired
    MainService mainService;

    public void start(String... args) throws IOException {
        long startTime = System.currentTimeMillis();

        mainService.doIt();

        long endTime = System.currentTimeMillis();
        Duration duration = Duration.ofMillis(endTime - startTime);
        System.out.println("Completed in " + duration.toString());
    }
}
