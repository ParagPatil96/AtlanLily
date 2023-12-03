package com.parag.lily.consumer;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

public class HeartBeat {
    private final AtomicReference<Instant> cursor;

    HeartBeat() {
        this.cursor = new AtomicReference<>(Instant.now());
    }

    void update(){
        cursor.set(Instant.now());
    }

    boolean isAlive(Duration duration){
        return Duration.between(cursor.get(), Instant.now()).compareTo(duration) < 0;
    }
}
