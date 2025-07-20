package com.example.pos.messaging.bus;


import com.example.common.model.Event;

@FunctionalInterface
public interface EventConsumer {
    void handle(Event event);
}

