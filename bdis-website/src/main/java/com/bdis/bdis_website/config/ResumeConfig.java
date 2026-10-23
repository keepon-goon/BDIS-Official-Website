package com.bdis.bdis_website.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;
import java.time.ZoneId;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(ResumeProperties.class)
public class ResumeConfig {
    @Bean
    public Clock resumeClock() {
        return Clock.system(ZoneId.of("Asia/Shanghai"));
    }

    @Bean(destroyMethod = "shutdownNow")
    public ThreadPoolExecutor resumeExecutor() {
        return new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2), task -> {
                    Thread thread = new Thread(task, "resume-converter");
                    thread.setDaemon(true);
                    return thread;
                }, new ThreadPoolExecutor.AbortPolicy());
    }
}
