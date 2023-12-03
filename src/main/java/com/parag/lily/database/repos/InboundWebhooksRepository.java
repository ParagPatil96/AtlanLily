package com.parag.lily.database.repos;

import com.parag.lily.database.DBSource;
import com.parag.lily.database.tables.InboundWebhook;
import jakarta.inject.Inject;

import java.sql.ResultSet;
import java.util.StringJoiner;

public class InboundWebhooksRepository extends BaseRepository<InboundWebhook> {

    @Inject
    public InboundWebhooksRepository() {
        super(DBSource.getPostgres());
    }

    @Override
    String tableName() {
        return "inbound_webhooks";
    }

    @Override
    Class<InboundWebhook> type() {
        return InboundWebhook.class;
    }

    public boolean addEntry(InboundWebhook webhook) {
        try {
            StringJoiner query = new StringJoiner(" ");
            query.add("INSERT INTO");
            query.add(tableName());
            query.add("( metadata_asset_id,endpoint,hmac_secret)");
            query.add("VALUES (");
            query.add("?,?,?");
            query.add(")");
            query.add("RETURNING *");
            ResultSet rs = execute(
                    query.toString(),
                    webhook.metadata_asset_id,
                    webhook.endpoint,
                    webhook.hmac_secret
            );
            return parseResultSet(rs).get(0).endpoint.equals(webhook.endpoint);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed while processing ResultSet, Error: %s", e));
        }
    }
}
