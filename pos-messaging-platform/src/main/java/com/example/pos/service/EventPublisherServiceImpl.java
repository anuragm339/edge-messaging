package com.example.pos.service;

import com.example.common.api.EventPublisherService;
import com.example.common.model.Event;
import com.example.pos.messaging.bus.EventBus;
import com.example.pos.storage.dao.EventLogJdbcDAO;
import com.example.pos.storage.model.EventLog;
import jakarta.inject.Singleton;

import java.time.LocalDateTime;

@Singleton
public class EventPublisherServiceImpl implements EventPublisherService {

private final EventBus eventBus;
private final EventLogJdbcDAO eventLogDao;

public EventPublisherServiceImpl(EventBus eventBus, EventLogJdbcDAO eventLogDao) {
    this.eventBus = eventBus;
    this.eventLogDao = eventLogDao;
}

/**
 * Accepts an Event, persists it (optional), and publishes it to the EventBus.
 */
public void publish(Event event) {
    try {
        // Step 1: Persist in database
        EventLog log = eventToLog(event);
        eventLogDao.save(log);

        // Step 2: Publish to memory queue
        eventBus.publish(event);

        // Success (can add logging here)
    } catch (Exception ex) {
        // TODO: Add retry/backpressure/dead-letter support
        throw new RuntimeException("Event publish failed", ex);
    }
}

private EventLog eventToLog(Event e) {
    EventLog log = new EventLog();
    log.setTopic(e.getTopic());
    log.setEventType(e.getType());
    log.setPayload(e.getPayload());
    log.setStatus("NEW");
    log.setCreatedAt(LocalDateTime.now());
    log.setUpdatedAt(null);
    return log;
}
}


