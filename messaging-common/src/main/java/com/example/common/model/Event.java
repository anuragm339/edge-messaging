package com.example.common.model;
import io.micronaut.serde.annotation.Serdeable;
@Serdeable
public class Event {
    private  String topic;
    private  String type;
    private  String payload;

    public Event() {
    }

    public Event(String topic, String type, String payload) {
        this.topic = topic;
        this.type = type;
        this.payload = payload;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPayload(String payload) {
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

