package com.example.common.model;


import io.micronaut.core.annotation.Introspected;
import jakarta.validation.constraints.NotBlank;

@Introspected
public class EventRequest {

    @NotBlank
    private String topic;

    private String type;

    @NotBlank
    private String payload;

    public @NotBlank String getTopic() {
        return topic;
    }

    public void setTopic(@NotBlank String topic) {
        this.topic = topic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public @NotBlank String getPayload() {
        return payload;
    }

    public void setPayload(@NotBlank String payload) {
        this.payload = payload;
    }
}

