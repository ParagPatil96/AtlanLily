package com.parag.lily.consumer.plugins;

import com.parag.lily.DefaultObjectMapper;
import com.parag.lily.Utility;
import com.parag.lily.consumer.Plugin;
import com.parag.lily.database.tables.OutboundWebhook;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.logging.Logger;


public class SlackPlugin extends Plugin {
    private static final Logger LOGGER = Utility.getLogger();
    protected final OutboundWebhook webhook;

    private final Client httpClient;

    public SlackPlugin(OutboundWebhook webhook) {
        super();
        this.webhook = webhook;
        this.httpClient = ClientBuilder.newClient().register(DefaultObjectMapper.JSON).register(LOGGER);
    }

    @Override
    protected void consume(String data) {
        String payload = DefaultObjectMapper.getJson(new SlackPayload(data));
        Invocation.Builder request = httpClient.target(webhook.url).request(MediaType.APPLICATION_JSON);

        try (Response response = request.post(Entity.json(payload))) {
            String responseBody = response.readEntity(String.class);
            LOGGER.info("Request Body: " + payload);
            LOGGER.info("Response Body: " + responseBody);
        }
    }

    public static class SlackPayload{
        public String text;

        public SlackPayload(String text) {
            this.text = text;
        }
    }
}
