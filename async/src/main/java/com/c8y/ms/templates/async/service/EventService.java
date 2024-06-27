package com.c8y.ms.templates.async.service;

import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import com.cumulocity.rest.representation.event.EventRepresentation;
import com.cumulocity.sdk.client.SDKException;
import com.cumulocity.sdk.client.event.EventApi;

import jdk.jfr.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    @Autowired
    EventApi eventApi;

    @Autowired
    MicroserviceSubscriptionsService subscriptionsService;

    //ExecutorService using CachedThreadPool
    ExecutorService executorService = Executors.newCachedThreadPool();

    //ExecutorService using Virtual Threads
    ExecutorService virtExecutorService = Executors.newVirtualThreadPerTaskExecutor();


    public List<EventRepresentation> getEvents0() {
        return eventApi.getEvents().get(10).getEvents();
    }

    /**
     * Method using CompletableFuture to call eventAPI asynchronously
     **/
    public CompletableFuture<List<EventRepresentation>> getEvents1()  {
        var eventFuture = new CompletableFuture();

        CompletableFuture.runAsync(() -> {
            try {
                subscriptionsService.runForEachTenant(() -> {
                    log.info("Retrieving events...");
                    eventFuture.complete(eventApi.getEvents().get(10).getEvents());
                    log.info("Retrieving events completed!");
                });
            } catch (SDKException e) {
                e.printStackTrace();
                CompletableFuture.failedFuture(e);
            }
        });

        return eventFuture;
    }

    /**
     * Method using @Async annotation to call whole method asynchronously
     **/
    @Async
    public CompletableFuture<List<EventRepresentation>> getEvents2()  {
        var eventFuture = new CompletableFuture();
        try {
            log.info("Retrieving events...");
            eventFuture.complete(eventApi.getEvents().get(10).getEvents());
            log.info("Retrieving events completed!");
        } catch (SDKException e) {
            eventFuture.completeExceptionally(e);
        }
        return eventFuture;
    }
    /**
     * Method using ExecutorService to create a new thread retrieving events
     **/
    public Future<List<EventRepresentation>> getEvents3() throws ExecutionException, InterruptedException {
        return executorService.submit( new EventRetrievalTask<>());
    }

    public class EventRetrievalTask<T> implements Callable<List<EventRepresentation>> {

        @Override
        public List<EventRepresentation> call() throws Exception {
            List<EventRepresentation> eventList = new ArrayList<>();
            subscriptionsService.runForEachTenant(() -> {
                log.info("Retrieving events...");
                eventList.addAll(eventApi.getEvents().get(10).getEvents());
                log.info("Retrieving events completed!");
            });

            return eventList;
        }
    }

    /**
     * Method using VirtualThreads + CompletableFuture to create a new thread retrieving events
     **/
    public Future<List<EventRepresentation>> getEvents4() throws InterruptedException {
        Future <List<EventRepresentation>> future =  virtExecutorService.submit(new Callable<List<EventRepresentation>>(){
            public List<EventRepresentation> call() throws Exception {
                return subscriptionsService.callForTenant(subscriptionsService.getTenant(), new Callable<List<EventRepresentation>>() {
                    public List<EventRepresentation> call() {
                        return eventApi.getEvents().get(10).getEvents();
                    }
                });
            }
        });
        return future;
    }
 }
