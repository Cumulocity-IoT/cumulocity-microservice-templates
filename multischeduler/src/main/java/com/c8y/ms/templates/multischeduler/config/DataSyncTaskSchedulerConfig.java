package com.c8y.ms.templates.multischeduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class DataSyncTaskSchedulerConfig {
	
    @Value("${scheduled.sync.pool.size:5}")
    protected Integer scheduledSyncPoolSize;
	
	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(scheduledSyncPoolSize);
		threadPoolTaskScheduler.setThreadNamePrefix("TaskScheduler-");
		return threadPoolTaskScheduler;
	}
}
