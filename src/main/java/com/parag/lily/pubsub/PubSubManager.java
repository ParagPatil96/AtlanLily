package com.parag.lily.pubsub;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.pubsub.v1.*;
import com.parag.lily.Utility;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.IOException;
import java.util.logging.Logger;


public class PubSubManager {
    private static final Logger LOGGER = Utility.getLogger();
    private static final int MB_20 = 1024 * 1024 * 20;
    final String projectId;
    final TransportChannelProvider channelProvider;
    final CredentialsProvider credentialsProvider;

    private final TopicAdminClient topicAdminClient;
    private final SubscriptionAdminClient subscriptionAdminClient;

    public PubSubManager() {
        this.projectId = "atlan-lily";
        this.channelProvider = channelProvider();
        this.credentialsProvider = NoCredentialsProvider.create();
        this.topicAdminClient = topicAdminClient(credentialsProvider, channelProvider);
        this.subscriptionAdminClient = subscriptionAdminClient(credentialsProvider, channelProvider);
    }

    TransportChannelProvider channelProvider() {
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8090").maxInboundMessageSize(MB_20).usePlaintext().build();
        return FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
    }

    public void createTopic(String topicId){
        TopicName topicName = TopicName.of(projectId, topicId);
        Topic topic = topicAdminClient.createTopic(topicName);
        LOGGER.info("Created topic: " + topic.getName());
    }

    public void createSubscription(String topicId, String subscriptionId) {
        ProjectTopicName topicName = ProjectTopicName.of(projectId, topicId);
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);

        Subscription subscription =
                subscriptionAdminClient.createSubscription(
                        Subscription.newBuilder()
                                .setName(subscriptionName.toString())
                                .setTopic(topicName.toString())
                                .setEnableExactlyOnceDelivery(true)
                                .build());

        LOGGER.info("Created a subscription " + subscriptionId);
    }

    private static TopicAdminClient topicAdminClient(CredentialsProvider credentialsProvider, TransportChannelProvider channelProvider){
        try {
            TopicAdminSettings settings = TopicAdminSettings.newBuilder()
                    .setCredentialsProvider(credentialsProvider)
                    .setTransportChannelProvider(channelProvider)
                    .build();

            return TopicAdminClient.create(settings);
        }catch (IOException e) {
            throw new RuntimeException("Failed while initializing TopicAdminClient",e);
        }
    }

    private static SubscriptionAdminClient subscriptionAdminClient(CredentialsProvider credentialsProvider, TransportChannelProvider channelProvider){
        try {
            SubscriptionAdminSettings settings = SubscriptionAdminSettings.newBuilder()
                    .setCredentialsProvider(credentialsProvider)
                    .setTransportChannelProvider(channelProvider)
                    .build();

            return SubscriptionAdminClient.create(settings);
        }catch (IOException e) {
            throw new RuntimeException("Failed while initializing TopicAdminClient",e);
        }
    }

    public void close(){
        topicAdminClient.close();
        subscriptionAdminClient.close();
    }
}
