package com.c8y.ms.templates.multischeduler.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.cumulocity.microservice.context.credentials.MicroserviceCredentials;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionRemovedEvent;

@Component
public class DataSyncTaskScheduler {
	private static final Logger log = LoggerFactory.getLogger(DataSyncTaskScheduler.class);

	private final DataSyncService dataSyncService;
	
	private final TaskScheduler taskScheduler;

    @Value("${scheduled.sync.rate.millis:60000}")
    protected Long snycRateMillis;
	
    private Map<String, ScheduledFuture<?>> scheduledFutureMap = new HashMap<>();
    
	public DataSyncTaskScheduler(DataSyncService dataSyncService, TaskScheduler taskScheduler) {
		this.dataSyncService = dataSyncService;
		this.taskScheduler = taskScheduler;
	}
	
    /**
     * Register for each tenant a separate scheduled task.
     *
     * @param event
     */
    @EventListener
    private void onSubscriptionAdded(final MicroserviceSubscriptionAddedEvent event) {
        MicroserviceCredentials credentials = event.getCredentials();
        
        log.info("Creating Scheduler task for tenant: {}", credentials.getTenant());
        ScheduledFuture<?> scheduleWithFixedDelay = taskScheduler.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {
				String tenant = getCredentials().getTenant();
				
				log.info("START, sync data for tenant: {}", tenant);
				dataSyncService.syncDataWithinContext(getCredentials());
				log.info("END, sync data for tenant: {}", tenant);
			}
			
			private MicroserviceCredentials getCredentials() {
				return credentials;
			}
			
		}, snycRateMillis);
        
        scheduledFutureMap.put(credentials.getTenant(), scheduleWithFixedDelay);
    }
    
    /**
     * Cancel tenant separate scheduled task.
     *
     * @param event
     */
    @EventListener
    private void onSubscriptionRemoved(final MicroserviceSubscriptionRemovedEvent event) {
        log.info("Cancel Scheduler task for tenant: {}", event.getTenant());
        
    	ScheduledFuture<?> scheduledFuture = scheduledFutureMap.get(event.getTenant());
    	if(scheduledFuture != null) {
    		scheduledFuture.cancel(true);
    	}
    }
}
