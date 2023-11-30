package com.parag.lily;

import java.net.URI;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;


public class Collector {
    private static final Logger LOGGER = Utility.getLogger();
    private static final URI BASE_URI = URI.create("http://localhost:8080/");

    private final Server server;
    public static void main(String[] args) throws Exception {
        Collector collector = new Collector();
        collector.startServer();
        collector.attachShutdownHook();
    }

    public Collector() {
        ResourceConfig config = new ResourceConfig().packages("com.parag.lily.routes").register(LoggingFeature.class);
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