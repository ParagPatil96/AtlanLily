package com.parag.lily.collector.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.parag.lily.DefaultObjectMapper;
import com.parag.lily.collector.exceptions.InvalidEndpointException;
import com.parag.lily.database.repos.InboundWebhooksRepository;
import com.parag.lily.database.tables.InboundWebhook;
import com.parag.lily.pojos.WebhookEvent;
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

    @Inject
    InboundWebhooksRepository webhooksRepository;

    @PathParam("endpoint")
    public String endpoint;
    @POST
    public Response post(String json) throws JsonProcessingException {
        try {
            if(isInValidTopic(endpoint)) throw new InvalidEndpointException("Unable to locate endpoint in db");
            publisherManager.publish(endpoint, getEventString(json));
            return successResponse();
        }catch (InvalidEndpointException e){
            return NotFoundResponse(String.format("Endpoint %s is not registered", endpoint));
        }
    }

    private String getEventString(String json){
        WebhookEvent event = new WebhookEvent();
        event.data = json;
        event.receivedAt = Instant.now();
        event.endpoint  = endpoint;

        return DefaultObjectMapper.getJson(event);
    }

    private boolean isInValidTopic(String topicId) {
        InboundWebhook webhook = new InboundWebhook();
        webhook.endpoint = topicId;
        return webhooksRepository.getEntity(webhook).isEmpty();
    }
}
