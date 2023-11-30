package com.parag.lily.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;


@Path("/health")
public class Health extends BaseRoute {

    @GET
    public Response get() throws JsonProcessingException {
        return successResponse("I am alive");
    }
}