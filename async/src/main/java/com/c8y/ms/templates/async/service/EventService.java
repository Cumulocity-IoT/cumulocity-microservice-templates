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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;


@Service
@EnableAsync
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


    public List<EventRepresentation> getEvents0()  {
        log.info("Simulating load...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<EventRepresentation> eventList = eventApi.getEvents().get(10).getEvents();
        log.info("# Events found: {}", eventList.size());
        log.info("Returning result!");
        return eventList;
    }

    /**
     * Method using CompletableFuture to call eventAPI asynchronously
     **/
    public CompletableFuture<List<EventRepresentation>> getEvents1() {
        var eventFuture = new CompletableFuture();
        CompletableFuture.runAsync(() -> {
                subscriptionsService.runForEachTenant(() -> {
                    try {
                        log.info("Simulating load...");
                        Thread.sleep(2000);
                        List<EventRepresentation> eventList = eventApi.getEvents().get(10).getEvents();
                        eventFuture.complete(eventApi.getEvents().get(10).getEvents());
                        log.info("# Events found: {}", eventList.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                        CompletableFuture.failedFuture(e);
                    }
                });

        });
        log.info("Returning result!");
        return eventFuture;
    }

    /**
     * Method using @Async annotation to call whole method asynchronously
     **/
    @Async
    public CompletableFuture<List<EventRepresentation>> getEvents2()  {
        var eventFuture = new CompletableFuture();
        subscriptionsService.runForEachTenant(() -> {
            try {
                log.info("Simulating load...");
                Thread.sleep(2000);
                List<EventRepresentation> eventList = eventApi.getEvents().get(10).getEvents();
                log.info("# Events found: {}", eventList.size());
                eventFuture.complete(eventList);
            } catch (Exception e) {
                eventFuture.completeExceptionally(e);
            }
        });
        log.info("Returning result!");
        return eventFuture;
    }
    /**
     * Method using ExecutorService to create a new thread retrieving events
     **/
    public Future<List<EventRepresentation>> getEvents3() throws ExecutionException, InterruptedException {
        log.info("Returning result!");
        return executorService.submit( new EventRetrievalTask<>());
    }

    public class EventRetrievalTask<T> implements Callable<List<EventRepresentation>> {

        @Override
        public List<EventRepresentation> call() throws Exception {
            List<EventRepresentation> eventList = new ArrayList<>();
            subscriptionsService.runForEachTenant(() -> {
                log.info("Simulating load...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                eventList.addAll(eventApi.getEvents().get(10).getEvents());
                log.info("# Events found: {}", eventList.size());

            });
            return eventList;
        }
    }

    /**
     * Method using VirtualThreads + CompletableFuture to create a new thread retrieving events
     **/
    public Future<List<EventRepresentation>> getEvents4() throws InterruptedException {
        log.info("Returning result!");
        return virtExecutorService.submit(new EventRetrievalTask<>());
    }



 }
