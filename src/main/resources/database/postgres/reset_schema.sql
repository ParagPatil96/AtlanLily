DROP TABLE IF EXISTS inbound_webhooks;
CREATE TABLE inbound_webhooks (
    metadata_asset_id TEXT NOT NULL,
    endpoint TEXT  NOT NULL,
    hmac_secret TEXT  NOT NULL,
    plugin TEXT  NOT NULL,
    PRIMARY KEY (metadata_asset_id)
);

DROP TABLE IF EXISTS outbound_webhooks;
CREATE TABLE outbound_webhooks (
    metadata_asset_id TEXT NOT NULL,
    plugin TEXT  NOT NULL,
    url TEXT  NOT NULL,
    hmac_secret TEXT  NOT NULL,
    PRIMARY KEY (metadata_asset_id, plugin)
);
