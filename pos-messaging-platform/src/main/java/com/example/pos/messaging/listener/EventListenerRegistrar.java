package com.example.pos.messaging.listener;

import com.example.common.annotation.EventListener;
import com.example.common.model.Event;
import com.example.pos.messaging.bus.EventBus;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.BeanContext;

import io.micronaut.inject.BeanDefinition;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collection;

@Singleton
public class EventListenerRegistrar {

    private static final Logger LOG = LoggerFactory.getLogger(EventListenerRegistrar.class);
    private final ApplicationContext applicationContext;
    private final EventBus eventBus;

    public EventListenerRegistrar(ApplicationContext applicationContext, EventBus eventBus) {
        this.applicationContext = applicationContext;
        this.eventBus = eventBus;
    }

    @PostConstruct
    public void registerAllListeners() {
        LOG.info("🔍 Scanning for @EventListener methods...");

        Collection<BeanDefinition<?>> allBeanDefs = applicationContext.getAllBeanDefinitions();

        for (BeanDefinition<?> beanDef : allBeanDefs) {
            Class<?> beanClass = beanDef.getBeanType();
            Method[] declaredMethods = beanClass.getDeclaredMethods();

            for (Method method : declaredMethods) {
                EventListener annotation = method.getAnnotation(EventListener.class);
                if (annotation == null) continue;

                if (method.getParameterCount() != 1) {
                    LOG.warn("⚠️  Skipping {}#{} - must have exactly one parameter.",
                            beanClass.getSimpleName(), method.getName());
                    continue;
                }

                Class<?> eventType = method.getParameterTypes()[0];

                if (eventType == Object.class || !eventType.isAnnotationPresent(Serdeable.class)) {
                    LOG.warn("⚠️  Skipping {}#{} - parameter type {} is not @Serdeable or is Object.class.",
                            beanClass.getSimpleName(), method.getName(), eventType.getSimpleName());
                    continue;
                }

                String topic = annotation.topic().trim();
                if (topic.isEmpty()) {
                    LOG.warn("⚠️  Skipping {}#{} - empty topic not allowed.",
                            beanClass.getSimpleName(), method.getName());
                    continue;
                }

                Object beanInstance = applicationContext.getBean(beanClass);
                method.setAccessible(true);

                LOG.info("✅ Registering event handler {}#{} for topic '{}'", beanClass.getSimpleName(), method.getName(), topic);

                // Register with EventBus
                eventBus.subscribe(topic, (event) -> {
                    try {
                        method.invoke(beanInstance, event);
                    } catch (Exception e) {
                        LOG.error("❌ Error invoking {}#{}: {}", beanClass.getSimpleName(), method.getName(), e.getMessage(), e);
                    }
                });
            }
        }

        LOG.info("🎉 Event listener registration completed.");
    }
}
