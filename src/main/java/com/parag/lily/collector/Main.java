package com.parag.lily.collector;

import java.net.URI;
import java.util.logging.Logger;

import com.parag.lily.Utility;

import com.parag.lily.database.DBSource;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;


public class Main {
    private static final Logger LOGGER = Utility.getLogger();
    private static final URI BASE_URI = URI.create("http://localhost:8001/");

    private final Server server;
    public static void main(String[] args) throws Exception {
        DBSource.initDataBases();
        Main main = new Main();
        main.startServer();
        main.attachShutdownHook();
    }

    public Main() {
        ResourceConfig config = new ResourceConfig()
                .packages("com.parag.lily.collector.routes")
                .register(LoggingFeature.class)
                .register(Binder.class);

        this.server = JettyHttpContainerFactory.createServer(BASE_URI, config, false);
    }

    public void startServer() throws Exception {
        server.start();
        LOGGER.info("Server Started.");
    }

    private void attachShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                LOGGER.info("Shutting down the Server...");
                server.stop();
                LOGGER.info("Shutdown complete");
            } catch (Exception e) {
                LOGGER.severe(e.toString());
            }
        }));
    }
}