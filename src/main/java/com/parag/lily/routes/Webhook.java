package com.parag.lily.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.parag.lily.DefaultObjectMapper;
import com.parag.lily.pojos.Event;
import com.parag.lily.pubsub.PublisherManager;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.time.Instant;

@Path("/webhook/{endpoint}")
public class Webhook extends BaseRoute{
    @Inject
    private PublisherManager publisherManager;

    @PathParam("endpoint")
    public String endpoint;
    @POST
    public Response post(String json) throws JsonProcessingException {
        publisherManager.publish(endpoint, getEventString(json));
        return successResponse();
    }

    private String getEventString(String json){
        Event event = new Event();
        event.data = json;
        event.receivedAt = Instant.now();
        event.endpoint  = endpoint;

        return DefaultObjectMapper.getJson(event);
    }
}
