package com.c8y.ms.templates.async.test;

import java.util.concurrent.*;

public class Test {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("Do something async!");
        });

        CompletableFuture<String> futureString = CompletableFuture.supplyAsync(() -> {
            return "Result of async processing";
        });
        futureString.get();
        futureString.thenApply(result -> {
           return result.toUpperCase();
        }).thenAccept(upperCaseString -> {
            System.out.println(upperCaseString);
        });

        CompletableFuture<String> future1 = new CompletableFuture();
        CompletableFuture.runAsync(() -> {
            future1.complete("Operation is finished!");
        });

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            System.out.println("Do something async!");
        });

        Callable<String> callableTask = () -> {
            return "Result of async Task";
        };
        Future<String> exFuture = executor.submit(callableTask);

        Thread.startVirtualThread(() -> {
            System.out.println("Do something async using a virtual Thread!");
        });

        Thread.Builder builder = Thread.ofVirtual().name("virtThread",0);
        Runnable task = () -> {
            System.out.println("Do something async using a virtual Thread!");
        };
        Thread t = builder.start(task);
        t.join();

    }



}
