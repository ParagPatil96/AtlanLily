package com.parag.lily.consumer.plugins;

import com.parag.lily.DefaultObjectMapper;
import com.parag.lily.consumer.Plugin;
import com.parag.lily.database.repos.AssetRepository;
import com.parag.lily.database.tables.Asset;
import com.parag.lily.pojos.WebhookEvent;

public class DefaultPlugin extends Plugin {
    private final AssetRepository assetRepository;

    public DefaultPlugin(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    protected void consume(String data) {
        WebhookEvent event = DefaultObjectMapper.parseJson(data, WebhookEvent.class);
        Asset asset = DefaultObjectMapper.parseJson(event.data, Asset.class);
        assetRepository.update(asset);
        LOGGER.info("data: " + data);
    }
}
