package com.example.pos.messaging.bus;


import com.example.common.model.Event;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.concurrent.*;
import java.util.*;

@Singleton
public class InMemoryEventBus implements EventBus {
    private final Map<String, CopyOnWriteArrayList<EventConsumer>> listeners = new ConcurrentHashMap<>();
    private final BlockingQueue<Event> queue;
    private final ExecutorService workers;

    @Inject
    public InMemoryEventBus(@Value("${eventbus.queue-capacity:1000}")int queueCapacity, @Value("${eventbus.worker-threads:4}") int workerThreads) {
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

