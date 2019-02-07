package com.battlezone.megamachines.events.game;

import com.battlezone.megamachines.networking.NewServer;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameUpdateEvent {

    private static final int POOL_SIZE = 25;
    private static BlockingQueue<GameUpdateEvent> pool = new LinkedBlockingQueue<>() {{
        for (int i = 0; i < POOL_SIZE; i++) {
            add(new GameUpdateEvent(ByteBuffer.allocate(NewServer.SERVER_TO_CLIENT_LENGTH)));
        }
    }};

    private final ByteBuffer buffer;

    private GameUpdateEvent(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public static GameUpdateEvent create(byte[] data) {
        GameUpdateEvent newUpdate = null;
        try {
            newUpdate = pool.take();
            ByteBuffer buffer = newUpdate.getBuffer();
            buffer.clear();
            buffer.put(data);
        } catch (InterruptedException e) {
            System.err.println("Interrupted whilst obtaining Game Update ByteBuffer");
        }
        return newUpdate;
    }

    public static void delete(GameUpdateEvent event) {
        pool.add(event);
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

}
