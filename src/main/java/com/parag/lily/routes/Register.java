package com.parag.lily.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.parag.lily.DefaultObjectMapper;
import com.parag.lily.pojos.RegisterEvent;
import com.parag.lily.pubsub.PubSubManager;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/register")
public class Register extends BaseRoute{
    @Inject PubSubManager pubSubManager;

    @POST
    public Response post(String json) throws JsonProcessingException {
        RegisterEvent event = DefaultObjectMapper.parseJson(json, RegisterEvent.class);
        pubSubManager.createTopic(event.endpoint);
        pubSubManager.createSubscription( event.endpoint,  event.endpoint);
        return successResponse();
    }
}
