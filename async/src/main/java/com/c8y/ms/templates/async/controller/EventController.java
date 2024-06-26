package com.c8y.ms.templates.async.controller;

import com.c8y.ms.templates.async.service.EventService;
import com.cumulocity.rest.representation.event.EventRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

import java.util.concurrent.ExecutionException;


@RestController
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    @Autowired
    EventService eventService;

    /**
     * Blocking Event Rest Controller calling blocking Service
     *
     * @return
     */
    @GetMapping(path = "/asyncEvents0", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EventRepresentation>> getEvents0() {
        log.info("Calling getEvents0!");
        return new ResponseEntity<>(eventService.getEvents0(), HttpStatus.OK) ;
    }


    /**
     * Blocking Event Rest Controller calling async Service using CompletableFuture
     *
     * @return
     */
    @GetMapping(path = "/asyncEvents1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EventRepresentation>> getEvents1() {
        log.info("Calling getEvents1!");
        return new ResponseEntity<>(eventService.getEvents1().join(), HttpStatus.OK) ;
    }

    /**
     * Non-Blocking Event Rest Controller calling async Service using @Async annotation
     *
     * @return
     */
    @GetMapping(path = "/asyncEvents2", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<List<EventRepresentation>>> getEvents2() {
        log.info("Calling getEvents2!");
        DeferredResult<ResponseEntity<List<EventRepresentation>>> result = new DeferredResult<>();
        eventService.getEvents2().thenApply(events -> result.setResult(new ResponseEntity<>(events, HttpStatus.OK)));
        return result;
    }

    /**
     * Non-Blocking Event Rest Controller calling async Service using Executor Service
     *
     * @return
     */
    @GetMapping(path = "/asyncEvents3", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<List<EventRepresentation>>> getEvents3() {
        log.info("Calling getEvents3!");
        DeferredResult<ResponseEntity<List<EventRepresentation>>> result = new DeferredResult<>();
        try {
            result.setResult(new ResponseEntity<>(eventService.getEvents3().get(), HttpStatus.OK));
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
        return result;
    }

    /**
     * Non-Blocking Event Rest Controller calling async Service using Virtual Threads
     *
     * @return
     */
    @GetMapping(path = "/asyncEvents4", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<List<EventRepresentation>>> getEvents4() {
        log.info("Calling getEvents4!");
        DeferredResult<ResponseEntity<List<EventRepresentation>>> result = new DeferredResult<>();
        eventService.getEvents4().thenApply(events -> result.setResult(new ResponseEntity<>(events, HttpStatus.OK)));
        return result;
    }
}
