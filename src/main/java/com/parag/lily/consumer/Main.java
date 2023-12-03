package com.parag.lily.consumer;

import com.parag.lily.DefaultObjectMapper;
import com.parag.lily.Utility;
import com.parag.lily.database.repos.InboundWebhooksRepository;
import com.parag.lily.database.repos.OutboundWebhooksRepository;
import com.parag.lily.database.tables.InboundWebhook;
import com.parag.lily.pojos.ConsumerWrapper;
import com.parag.lily.pojos.WebhookEvent;
import com.parag.lily.pubsub.PubSubManager;
import com.parag.lily.pubsub.SubscriberManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import static com.parag.lily.Utility.getSystemProperty;
import static com.parag.lily.pubsub.SubscriberManager.MAX_OUTSTANDING_MSG_COUNT;

public class Main {
    private static final Logger LOGGER = Utility.getLogger();
    private final InboundWebhooksRepository inboundWebhooksRepository;
    private final OutboundWebhooksRepository outboundWebhooksRepository;
    private final SubscriberManager subscriberManager;
    private final HeartBeat heartBeat;
    private final BlockingQueue<ConsumerWrapper> eventQueue = new ArrayBlockingQueue<>((int) MAX_OUTSTANDING_MSG_COUNT);
    private final Map<String, Plugin> plugins = new HashMap<>();

    public Main(String subscriptionId) {
        this.subscriberManager = new SubscriberManager(new PubSubManager(), subscriptionId, this::consume);
        this.inboundWebhooksRepository =  new InboundWebhooksRepository();
        this.outboundWebhooksRepository =  new OutboundWebhooksRepository();
        this.heartBeat =  new HeartBeat();
    }

    public static void main(String[] args) {
        String subscriptionId = getSystemProperty("PUBSUB_SUBSCRIPTION_ID", String.class);
        Main main = new Main(subscriptionId);
        main.attachShutdownHook();
        main.start();
    }

    private void start(){
        subscriberManager.startProcessing();
        heartBeat.update();

        // we check if heartbeat of event fetching got updated in last 10 seconds or not.
        // if its not updated, then we can safely close the consumer as there are no events in queue
        while(heartBeat.isAlive(Duration.ofSeconds(10))) {
            consumeEvents();
        }
    }

    private void consumeEvents() {
        for (ConsumerWrapper event : drainQueue()) {
            Plugin plugin = plugins.computeIfAbsent(event.event.endpoint, this::getPlugin);
            plugin.consume(event.event.data);
            event.ack.run();
        }
    }

    private Plugin getPlugin(String endpoint) {
        InboundWebhook inboundWebhook =  inboundWebhooksRepository.getEntity(new InboundWebhook(endpoint)).getFirst();

        return Plugin.get(inboundWebhook, outboundWebhooksRepository);
    }

    private void consume(String data, Runnable ack){
        WebhookEvent event = DefaultObjectMapper.parseJson(data, WebhookEvent.class);
        eventQueue.offer(new ConsumerWrapper(event, ack));
    }

    private List<ConsumerWrapper> drainQueue(){
        List<ConsumerWrapper> events =  new ArrayList<>();
        eventQueue.drainTo(events);
        return events;
    }

    private void attachShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                LOGGER.info("Shutting down the Server...");
                subscriberManager.close();
                consumeEvents(); // consumer remaining events

                // todo: check if all queued requests in jetty server are processed before closing, inorder to add graceful shutdown
                LOGGER.info("Shutdown complete");
            } catch (Exception e) {
                LOGGER.severe(e.toString());
            }
        }));
    }
}
