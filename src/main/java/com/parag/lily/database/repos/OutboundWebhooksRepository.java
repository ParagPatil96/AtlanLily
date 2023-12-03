package com.parag.lily.database.repos;

import com.parag.lily.database.DBSource;
import com.parag.lily.database.tables.OutboundWebhook;

import java.sql.ResultSet;
import java.util.StringJoiner;

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

    public boolean addEntry(OutboundWebhook webhook) {
        try {
            StringJoiner query = new StringJoiner(" ");
            query.add("INSERT INTO");
            query.add(tableName());
            query.add("( metadata_asset_id,url,hmac_secret, plugin)");
            query.add("VALUES (");
            query.add("?,?,?,?");
            query.add(")");
            query.add("RETURNING *");
            ResultSet rs = execute(
                    query.toString(),
                    webhook.metadata_asset_id,
                    webhook.url,
                    webhook.hmac_secret,
                    webhook.plugin
            );
            return parseResultSet(rs).get(0).metadata_asset_id.equals(webhook.metadata_asset_id);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed while processing ResultSet, Error: %s", e));
        }
    }
}
