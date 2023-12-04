package com.parag.lily.database.tables;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class Asset {
    public String metadata_asset_id;
    public Integer table_size;
    public Integer row_count;

    @JsonDeserialize(using = CrdBooleanDeserializer.class)
    public Boolean pii;
}
