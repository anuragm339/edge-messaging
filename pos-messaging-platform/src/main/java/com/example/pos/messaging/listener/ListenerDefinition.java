package com.example.pos.messaging.listener;


import com.example.common.model.Event;

import java.lang.reflect.Method;

public class ListenerDefinition {
    private final Object target;
    private final Method method;
    private final String topic;

    public ListenerDefinition(Object target, Method method, String topic) {
        this.target = target;
        this.method = method;
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void invoke(Event event) {
        try {
            method.invoke(target, event);
        } catch (Exception e) {
            // Log or escalate
            throw new RuntimeException("Failed to invoke event listener: " + method.getName(), e);
        }
    }
}

