package com.example.pos.api.controller;


import com.example.common.api.EventPublisherService;
import com.example.common.model.Event;
import com.example.common.model.EventRequest;
import io.micronaut.http.annotation.*;
import io.micronaut.http.HttpResponse;

import jakarta.inject.Inject;
import jakarta.validation.Valid;

@Controller("/events")
public class EventController {

    private final EventPublisherService eventPublisherService;

    @Inject
    public EventController(EventPublisherService eventPublisherService) {
        this.eventPublisherService = eventPublisherService;
    }

    @Post("/")
    public HttpResponse<?> publish(@Body @Valid EventRequest eventRequest) {
        try {
            Event event = new Event(
                    eventRequest.getTopic(),
                    eventRequest.getType(),
                    eventRequest.getPayload()
            );

            eventPublisherService.publish(event);

            return HttpResponse.accepted(); // 202 Accepted
        } catch (Exception ex) {
            return HttpResponse.serverError("Publishing failed: " + ex.getMessage());
        }
    }
}

