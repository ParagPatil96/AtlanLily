DROP TABLE IF EXISTS assets;
COMMIT;
CREATE TABLE assets (
    metadata_asset_id TEXT NOT NULL,
    table_size INT  NOT NULL,
    row_count INT  NOT NULL,
    pii BOOL NOT NULL,
    PRIMARY KEY (metadata_asset_id)
);