DROP TABLE IF EXISTS assets;
CREATE TABLE inbound_webhooks (
    metadata_asset_id TEXT NOT NULL,
    data TEXT  NOT NULL,
    PRIMARY KEY (metadata_asset_id)
);