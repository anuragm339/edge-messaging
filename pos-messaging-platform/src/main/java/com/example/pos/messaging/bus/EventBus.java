package com.example.pos.messaging.bus;

import com.example.common.model.Event;

;

public interface EventBus {
    void publish(Event event);
    void subscribe(String topic, EventConsumer consumer);
    void unsubscribe(String topic, EventConsumer consumer);
}

