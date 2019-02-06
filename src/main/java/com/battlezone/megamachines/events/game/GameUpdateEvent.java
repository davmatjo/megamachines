package com.battlezone.megamachines.events.game;

import com.battlezone.megamachines.networking.NewServer;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class GameUpdateEvent {

    private static final int POOL_SIZE = 15;
    private static BlockingQueue<ByteBuffer> pool = new LinkedBlockingQueue<>() {{
        for (int i = 0; i < POOL_SIZE; i++) {
            add(ByteBuffer.allocate(NewServer.SERVER_TO_CLIENT_LENGTH));
        }
    }};

    public static ByteBuffer create(byte[] data) {
        ByteBuffer newBuffer = null;
        try {
            newBuffer = pool.take();
            newBuffer.clear();
            newBuffer.put(data);
        } catch (InterruptedException e) {
            System.err.println("Interrupted whilst obtaining Game Update ByteBuffer");
        }
        return newBuffer;
    }

    public static void delete(ByteBuffer buffer) {
        pool.add(buffer);
    }

}
