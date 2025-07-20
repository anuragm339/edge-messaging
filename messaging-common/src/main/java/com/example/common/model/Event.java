package com.example.common.model;
import io.micronaut.serde.annotation.Serdeable;
@Serdeable
public class Event {
    private final String topic;
    private final String type;
    private final String payload;

    public Event(String topic, String type, String payload) {
        this.topic = topic;
        this.type = type;
        this.payload = payload;
    }

    public String getTopic() {
        return topic;
    }

    public String getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }
}

