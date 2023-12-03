package com.parag.lily.collector;

import com.parag.lily.database.repos.InboundWebhooksRepository;
import com.parag.lily.pubsub.PubSubManager;
import com.parag.lily.pubsub.PublisherManager;
import jakarta.inject.Singleton;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class Binder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(InboundWebhooksRepository.class).to(InboundWebhooksRepository.class).in(Singleton.class);
        bind(PubSubManager.class).to(PubSubManager.class).in(Singleton.class);
        bind(PublisherManager.class).to(PublisherManager.class).in(Singleton.class);
    }
}
