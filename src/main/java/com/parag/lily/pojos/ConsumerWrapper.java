package com.parag.lily.pojos;

public class ConsumerWrapper {
    public WebhookEvent event;
    public Runnable ack;

    public ConsumerWrapper(WebhookEvent event, Runnable ack) {
        this.event = event;
        this.ack = ack;
    }
}
