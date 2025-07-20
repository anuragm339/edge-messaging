package com.example.pos.messaging.bus;

import com.example.pos.messaging.model.Event;

import java.util.concurrent.*;
import java.util.*;

public class InMemoryEventBus implements EventBus {
    private final Map<String, CopyOnWriteArrayList<EventConsumer>> listeners = new ConcurrentHashMap<>();
    private final BlockingQueue<Event> queue;
    private final ExecutorService workers;

    public InMemoryEventBus(int queueCapacity, int workerThreads) {
        this.queue = new LinkedBlockingQueue<>(queueCapacity);
        this.workers = Executors.newFixedThreadPool(workerThreads);

        // Start worker threads for consuming events
        for (int i = 0; i < workerThreads; i++) {
            workers.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Event event = queue.take();
                        deliver(event);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
    }

    @Override
    public void publish(Event event) {
        if (!queue.offer(event)) {
            // Handle backpressure or queue overflow (e.g., drop, log, block, or escalate)
        }
    }

    @Override
    public void subscribe(String topic, EventConsumer consumer) {
        listeners.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(consumer);
    }

    @Override
    public void unsubscribe(String topic, EventConsumer consumer) {
        Optional.ofNullable(listeners.get(topic)).ifPresent(list -> list.remove(consumer));
    }

    private void deliver(Event event) {
        List<EventConsumer> consumers = listeners.getOrDefault(event.getTopic(), new CopyOnWriteArrayList<>());
        for (EventConsumer consumer : consumers) {
            try {
                consumer.handle(event);
            } catch (Exception ex) {
                // Log error; consider re-queue or dead-letter logic here
            }
        }
    }
}

