package com.parag.lily.database.repos;

import com.parag.lily.database.DBSource;
import com.parag.lily.database.tables.Asset;
import com.parag.lily.database.tables.InboundWebhook;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.StringJoiner;

public class AssetRepository extends BaseRepository<Asset>{
    public AssetRepository() {
        super(DBSource.getCockroachDb());
    }

    @Override
    String tableName() {
        return "assets";
    }

    @Override
    Class<Asset> type() {
        return Asset.class;
    }

    public boolean update(Asset asset) {
        try {
            StringJoiner query = new StringJoiner(" ");
            query.add("UPDATE");
            query.add(tableName());
            query.add("SET table_size=? ,row_count=?, pii=?");
            query.add("WHERE metadata_asset_id=?");
            query.add("RETURNING *");
            ResultSet rs = execute(
                    query.toString(),
                    asset.table_size,
                    asset.row_count,
                    asset.pii,
                    asset.metadata_asset_id
            );
            return parseResultSet(rs).get(0).metadata_asset_id.equals(asset.metadata_asset_id);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed while processing ResultSet, Error: %s", e));
        }
    }
}
