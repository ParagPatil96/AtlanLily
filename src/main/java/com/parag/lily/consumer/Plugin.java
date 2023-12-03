package com.parag.lily.consumer;

import com.parag.lily.Utility;
import com.parag.lily.consumer.plugins.AtlanPlugin;
import com.parag.lily.consumer.plugins.SlackPlugin;

import java.util.logging.Logger;

public abstract class Plugin {
    protected static final Logger LOGGER = Utility.getLogger();
    protected abstract void consume(String data);

    public static Plugin get(String name){
        return switch (name) {
            case "atlan" -> new AtlanPlugin();
            case "slack" -> new SlackPlugin();
            default -> throw new RuntimeException("Unsupported plugin type");
        };
    }
}
