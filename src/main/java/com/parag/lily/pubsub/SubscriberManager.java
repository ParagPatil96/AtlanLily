package com.parag.lily.pubsub;

import com.google.api.gax.batching.FlowControlSettings;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import java.util.function.BiConsumer;

public class SubscriberManager implements AutoCloseable {
    public static final long MAX_OUTSTANDING_MSG_COUNT = 50000L;
    private final PubSubManager pubSubManager;
    private final ProjectSubscriptionName subscription;
    private final Subscriber subscriber;
    private final BiConsumer<String, Runnable> msgConsumer;

    public SubscriberManager(PubSubManager pubSubManager, String subscriptionId, BiConsumer<String, Runnable> msgConsumer) {
        this.pubSubManager = pubSubManager;
        this.msgConsumer = msgConsumer;
        this.subscription =  ProjectSubscriptionName.of(pubSubManager.projectId, subscriptionId);
        this.subscriber = getSubscriber();
    }

    public Subscriber getSubscriber() {
        // Instantiate an asynchronous message receiver.
        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    msgConsumer.accept(message.getData().toStringUtf8(), consumer::ack);
                };

        FlowControlSettings flowControlSettings = FlowControlSettings.newBuilder()
                .setMaxOutstandingElementCount(MAX_OUTSTANDING_MSG_COUNT)
                .build();

        return Subscriber.newBuilder(subscription, receiver)
                .setChannelProvider(pubSubManager.channelProvider)
                .setCredentialsProvider(pubSubManager.credentialsProvider)
                .setParallelPullCount(2)
                .setFlowControlSettings(flowControlSettings)
                .build();
    }

    public void startProcessing(){
        subscriber.startAsync().awaitRunning();
    }
    @Override
    public void close() throws Exception {
        subscriber.stopAsync();
    }
}
