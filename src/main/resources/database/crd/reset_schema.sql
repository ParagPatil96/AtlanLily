DROP TABLE IF EXISTS assets;
CREATE TABLE assets (
    metadata_asset_id TEXT NOT NULL,
    data TEXT  NOT NULL,
    PRIMARY KEY (metadata_asset_id)
);