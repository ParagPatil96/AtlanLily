package com.parag.lily.collector.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.parag.lily.DefaultObjectMapper;
import com.parag.lily.database.repos.InboundWebhooksRepository;
import com.parag.lily.database.tables.InboundWebhook;
import com.parag.lily.pubsub.PubSubManager;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/register")
public class Register extends BaseRoute{
    @Inject PubSubManager pubSubManager;
    @Inject InboundWebhooksRepository webhooksRepository;

    @Path("/inbound")
    @POST
    public Response post(String json) throws JsonProcessingException {
        InboundWebhook inboundWebhook = DefaultObjectMapper.parseJson(json, InboundWebhook.class);

        //Todo: add validation checks for inboundWebhook
        webhooksRepository.addEntry(inboundWebhook);

        pubSubManager.createTopic(inboundWebhook.endpoint);
        pubSubManager.createSubscription( inboundWebhook.endpoint,  inboundWebhook.endpoint);

        return successResponse();
    }
}
