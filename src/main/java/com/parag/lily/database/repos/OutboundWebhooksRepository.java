package com.parag.lily.database.repos;

import com.parag.lily.database.DBSource;
import com.parag.lily.database.tables.OutboundWebhook;

public class OutboundWebhooksRepository extends BaseRepository<OutboundWebhook> {

    public OutboundWebhooksRepository() {
        super(DBSource.getPostgres());
    }

    @Override
    String tableName() {
        return "outbound_webhooks";
    }

    @Override
    Class<OutboundWebhook> type() {
        return OutboundWebhook.class;
    }
}
