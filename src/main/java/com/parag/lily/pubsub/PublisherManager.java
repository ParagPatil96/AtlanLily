package com.parag.lily.pubsub;
import com.google.api.core.ApiFuture;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.FixedExecutorProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import com.parag.lily.Utility;
import com.parag.lily.collector.exceptions.InvalidEndpointException;
import com.parag.lily.database.repos.InboundWebhooksRepository;
import com.parag.lily.database.tables.InboundWebhook;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public class PublisherManager {
    private static final Logger LOGGER = Utility.getLogger();
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(4);
    private static final ExecutorProvider EXECUTOR_PROVIDER = FixedExecutorProvider.create(EXECUTOR_SERVICE);

    private final ConcurrentHashMap<String, Publisher> publisherCache = new ConcurrentHashMap<>();

    @Inject PubSubManager pubSubManager;

    private Publisher buildPublisher(String topicId){
        try {
            TopicName topicName = TopicName.of(pubSubManager.projectId, topicId);
            return Publisher.newBuilder(topicName)
                    .setChannelProvider(pubSubManager.channelProvider)
                    .setCredentialsProvider(pubSubManager.credentialsProvider)
                    .setExecutorProvider(EXECUTOR_PROVIDER)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Failed while creating publisher for topic " + topicId, e);
        }
    }

    public void publish(String topicId, String event){
        Publisher publisher = publisherCache.computeIfAbsent(topicId, this::buildPublisher);

        ByteString data = ByteString.copyFromUtf8(event);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

        // Once published, returns a server-assigned message id (unique within the topic)
        ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);

        try {
            String messageId = messageIdFuture.get();
            LOGGER.info("Published message ID: " + messageId);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed while publishing event", e);
        }
    }

    public void close() {
        publisherCache.values().forEach(Publisher::shutdown);
    }
}
