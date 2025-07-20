package com.example.common.api;

import com.example.common.model.Event;

public interface EventPublisherService {
    void publish(Event event);
    // other methods as needed
}

