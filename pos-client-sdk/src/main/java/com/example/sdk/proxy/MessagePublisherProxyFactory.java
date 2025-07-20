package com.example.sdk.proxy;


import com.example.common.annotation.MessagePublisher;
import com.example.common.api.EventPublisherService;
import com.example.common.model.Event;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Context;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Context
@Singleton
public class MessagePublisherProxyFactory {

    private final ApplicationContext context;
    private final EventPublisherService eventPublisherService;

    public MessagePublisherProxyFactory(ApplicationContext context, EventPublisherService eventPublisherService) {
        this.context = context;
        this.eventPublisherService = eventPublisherService;
    }

    @PostConstruct
    public void init() {
        context.getBeansOfType(Object.class).forEach(bean -> {
            for (Method method : bean.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(MessagePublisher.class)) {

                    MessagePublisher annotation = method.getAnnotation(MessagePublisher.class);

                    String topic = annotation.topic();
                    String type = annotation.eventType().isEmpty() ? method.getName() : annotation.eventType();

                    // Wrap and register, or log that this publisher is active
                    // In production, you'd register an invocation handler or inject proxy
                    System.out.println("Registered publisher for topic: " + topic + ", type: " + type);
                }
            }
        });
    }

    // Optional: create method interceptor using AOP/proxying
    public static Object createProxy(Object original, Method method, MessagePublisher annotation, EventPublisherService publisherService) {
        return Proxy.newProxyInstance(
                original.getClass().getClassLoader(),
                original.getClass().getInterfaces(),
                (proxy, m, args) -> {
                    if (m.equals(method)) {
                        String payload = (String) args[0];  // assume first arg is payload
                        Event event = new Event(annotation.topic(), annotation.eventType(), payload);
                        publisherService.publish(event);
                        return null;  // or return custom response
                    } else {
                        return m.invoke(original, args);
                    }
                }
        );
    }
}

