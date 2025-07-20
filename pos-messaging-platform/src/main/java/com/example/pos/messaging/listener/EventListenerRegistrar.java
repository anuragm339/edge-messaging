package com.example.pos.messaging.listener;

import com.example.pos.messaging.annotation.EventListener;
import com.example.pos.messaging.bus.EventBus;
import com.example.pos.messaging.model.Event;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Context;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

import java.lang.reflect.Method;
import java.util.*;

@Context
@Singleton
public class EventListenerRegistrar {

    private final ApplicationContext context;
    private final EventBus eventBus;

    public EventListenerRegistrar(ApplicationContext context, EventBus eventBus) {
        this.context = context;
        this.eventBus = eventBus;
    }

    @PostConstruct
    public void registerAllListeners() {
        Collection<Object> beans = context.getBeansOfType(Object.class);

        for (Object bean : beans) {
            Method[] methods = bean.getClass().getDeclaredMethods();

            for (Method method : methods) {
                if (method.isAnnotationPresent(EventListener.class)) {
                    EventListener annotation = method.getAnnotation(EventListener.class);

                    // Validation: method should accept single Event param
                    if (method.getParameterCount() != 1 ||
                            !method.getParameterTypes()[0].equals(Event.class)) {
                        throw new IllegalArgumentException(
                                "@EventListener method must have single Event parameter: " + method
                        );
                    }

                    ListenerDefinition definition = new ListenerDefinition(bean, method, annotation.topic());

                    // Subscribe
                    eventBus.subscribe(annotation.topic(), event -> definition.invoke(event));
                }
            }
        }
    }
}

