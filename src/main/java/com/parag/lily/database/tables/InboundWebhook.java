package com.parag.lily.database.tables;

public class InboundWebhook {
    public String metadata_asset_id;
    public String endpoint;
    public String plugin;
    public String hmac_secret;

    public InboundWebhook(String endpoint) {
        this.endpoint = endpoint;
    }

    // for jackson
    public InboundWebhook() {
    }
}
