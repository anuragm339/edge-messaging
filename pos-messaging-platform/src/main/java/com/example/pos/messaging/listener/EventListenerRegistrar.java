package com.example.pos.messaging.listener;

import com.example.pos.messaging.bus.EventBus;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Context;
import com.example.common.annotation.EventListener;

import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.serde.annotation.Serdeable;
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
        Collection<Object> beans;
        try {
            beans = context.getBeansOfType(Object.class, Qualifiers.byStereotype(EventListener.class));
            for (Object bean : beans) {
                Method[] methods = bean.getClass().getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(EventListener.class)) {
                        EventListener annotation = method.getAnnotation(EventListener.class);

                        if (method.getParameterCount() == 1) {
                            Class<?> clazz = method.getParameterTypes()[0];

                            // Defensive: Skip raw Object.class, interfaces, and types lacking @Serdeable
                            if (clazz == Object.class) {
                                System.err.println("Skipping registration for method " + method +
                                        " because parameter type is raw Object.class (not serializable).");
                                continue;
                            }
                            if (clazz.isInterface()) {
                                System.err.println("Skipping registration for method " + method +
                                        " because parameter type " + clazz + " is an interface and not concrete.");
                                continue;
                            }
                            if (!clazz.isAnnotationPresent(Serdeable.class)) {
                                System.err.println("Skipping registration for method " + method +
                                        " because parameter type " + clazz +
                                        " is not annotated with @Serdeable.");
                                continue;
                            }

                            String topic = annotation.topic();
                            eventBus.subscribe(topic, event -> {
                                try {
                                    method.setAccessible(true);
                                    method.invoke(bean, event);
                                } catch (Exception e) {
                                    System.err.println("Error invoking listener on " + bean.getClass() +
                                            ", method: " + method + ": " + e.getMessage());
                                }
                            });
                        } else {
                            System.err.println("Listener method " + method +
                                    " must take exactly one @Serdeable parameter.");
                        }
                    }
                }
            }
        }catch (Exception e) {
            System.err.println("EventListenerRegistrar: Unable to resolve beans—" + e.getMessage());
        }

    }
}

