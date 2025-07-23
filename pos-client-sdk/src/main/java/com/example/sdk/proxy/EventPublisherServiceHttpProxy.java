package com.example.sdk.proxy;


import com.example.common.api.EventPublisherService;
import com.example.common.model.Event;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.MediaType;
import jakarta.inject.Singleton;
import jakarta.inject.Inject;


/**
 * HTTP Proxy implementation of EventPublisherService for remote event publishing.
 */
@Singleton
public class EventPublisherServiceHttpProxy implements EventPublisherService {
    private final HttpClient httpClient;
    private final String platformBaseUrl;

    @Inject
    public EventPublisherServiceHttpProxy(
            @Client("${edge.messaging.platform.url:`http://localhost:8080`}") HttpClient httpClient
    ) {
        this.httpClient = httpClient;
        this.platformBaseUrl = "http://localhost:8080"; // Or inject via configuration
    }


    @Override
    public void publish(Event event) {
        String endpoint = platformBaseUrl + "/events";

        HttpResponse<?> response = httpClient.toBlocking().exchange(
                io.micronaut.http.HttpRequest.POST(endpoint, event)
                        .contentType(MediaType.APPLICATION_JSON), Object.class);

        if (!response.getStatus().equals(HttpStatus.OK) && !response.getStatus().equals(HttpStatus.ACCEPTED)) {
            throw new RuntimeException("Failed to publish event: HTTP " + response.getStatus());
        }
    }
}
