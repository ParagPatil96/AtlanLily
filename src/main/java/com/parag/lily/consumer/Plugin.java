package com.parag.lily.consumer;

import com.parag.lily.Utility;
import com.parag.lily.consumer.plugins.DefaultPlugin;
import com.parag.lily.consumer.plugins.SlackPlugin;
import com.parag.lily.consumer.plugins.TeamsPlugin;
import com.parag.lily.database.repos.AssetRepository;
import com.parag.lily.database.repos.OutboundWebhooksRepository;
import com.parag.lily.database.tables.InboundWebhook;
import com.parag.lily.database.tables.OutboundWebhook;

import java.util.logging.Logger;

public abstract class Plugin {
    protected static final Logger LOGGER = Utility.getLogger();
    private static final AssetRepository ASSET_REPOSITORY = new AssetRepository();

    protected abstract void consume(String data);

    public static Plugin get(InboundWebhook inboundWebhook, OutboundWebhooksRepository webhooksRepository){
        if(inboundWebhook.plugin.equals("default")) {
            return new DefaultPlugin(ASSET_REPOSITORY);
        }

        OutboundWebhook outboundWebhook = webhooksRepository
                .getEntity(new OutboundWebhook(inboundWebhook.metadata_asset_id, inboundWebhook.plugin))
                .getFirst();

        return switch (outboundWebhook.plugin) {
            case "slack" -> new SlackPlugin(outboundWebhook);
            case "teams" -> new TeamsPlugin();
            default -> throw new RuntimeException("Unsupported plugin type");
        };
    }
}
