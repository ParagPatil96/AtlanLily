package com.parag.lily;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.logging.Logger;


public class DefaultObjectMapper {

    private static final Logger LOGGER = Utility.getLogger();
    public static ObjectMapper JSON = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        return mapper;
    }

    public static String getJson(Object obj) {
        try {
            return JSON.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            String error = String.format("Failed while converting %s to JSON String, Error: %s", obj.getClass(), e);
            LOGGER.severe(error);
            throw new RuntimeException(error);
        }
    }

    public static <T> T parseJson(String json, Class<T> type) {
        try {
            return JSON.readValue(json, type);
        } catch (JsonProcessingException e) {
            String error = String.format("Failed parsing %s from JSON: %s \nError: %s", type, json, e);
            LOGGER.severe(error);
            throw new RuntimeException(error);
        }
    }
}
