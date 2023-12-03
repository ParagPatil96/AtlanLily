package com.parag.lily.consumer.plugins;

import com.parag.lily.consumer.Plugin;

public class SlackPlugin extends Plugin {
    @Override
    protected void consume(String data) {
        LOGGER.info("data: " + data);
    }
}
