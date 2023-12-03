package com.parag.lily.database.tables;

public class OutboundWebhook {
    public String metadata_asset_id;
    public String url;
    public String plugin;
    public String hmac_secret;

    public OutboundWebhook(String metadata_asset_id, String plugin) {
        this.metadata_asset_id = metadata_asset_id;
        this.plugin = plugin;
    }

    //for jackson
    public OutboundWebhook() {
    }
}
