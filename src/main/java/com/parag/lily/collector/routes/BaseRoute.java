package com.parag.lily.collector.routes;

import static com.parag.lily.DefaultObjectMapper.JSON;
import static jakarta.ws.rs.core.Response.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.parag.lily.DefaultObjectMapper;
import com.parag.lily.Utility;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class BaseRoute {

	private static final Logger LOGGER = Utility.getLogger();
	protected static final JavaType MAP_TYPE = JSON.getTypeFactory().constructMapType(Map.class, String.class, String.class);

	@Context
	protected HttpHeaders context;

	@HeaderParam("Origin")
	protected String origin;

	protected ObjectNode objectNode(Map<String, Object> entries) {
		ObjectNode node = JSON.createObjectNode();
		entries.forEach((k, v) -> node.put(k, JSON.convertValue(v, JsonNode.class)));
		return node;
	}

	protected Response successResponse() throws JsonProcessingException {
		return successResponse("OK");
	}

	protected Response successResponse(Object entity) throws JsonProcessingException {
		return successResponse(entity, new HashMap<>());
	}

	protected Response successResponse(Object entity, Map<String, String> headers) throws JsonProcessingException {
		return response(entity, headers, Status.OK);
	}

	protected Response unauthorisedResponse(String message) throws JsonProcessingException {
		return response(message, new HashMap<>(), Status.UNAUTHORIZED);
	}

	protected Response badRequest(String message) throws JsonProcessingException {
		return response(message, new HashMap<>(), Status.BAD_REQUEST);
	}

	protected Response internalServerErrorResponse(Object entity, Map<String, String> headers)
		throws JsonProcessingException {
		return response(entity, new HashMap<>(), Status.INTERNAL_SERVER_ERROR);
	}

	protected Response response(Object entity, Map<String, String> headers, Status status)
		throws JsonProcessingException {
		// Header required for CORS
		headers.put("Access-Control-Allow-Origin", origin);
		headers.put("Access-Control-Allow-Headers", HttpHeaders.CONTENT_TYPE);
		headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

		Response.ResponseBuilder builder = Response.status(status);
		builder.entity(DefaultObjectMapper.getJson(entity));
		headers.forEach(builder::header);
		return builder.build();
	}
}
