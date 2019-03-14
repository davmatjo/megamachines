package com.battlezone.megamachines.events.game;

import com.battlezone.megamachines.events.Pooled;
import com.battlezone.megamachines.networking.server.Server;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameUpdateEvent implements Pooled {

    private static final int POOL_SIZE = 25;
    private static BlockingQueue<GameUpdateEvent> pool = new LinkedBlockingQueue<>() {{
        for (int i = 0; i < POOL_SIZE; i++) {
            add(new GameUpdateEvent(ByteBuffer.allocate(Server.SERVER_TO_CLIENT_LENGTH)));
        }
    }};

    private final ByteBuffer buffer;

    private GameUpdateEvent(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    /**
     * Gets a GameUpdateEvent from the pool and fills it with the data
     * @param data Data to fill the event with
     * @return A GameUpdateEvent filled with the data
     */
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

    /**
     * Returns a GameUpdateEvent to the pool
     */
    public void delete() {
        pool.add(this);
    }

    /**
     * @return The data of this event in the form of a ByteBuffer
     */
    public ByteBuffer getBuffer() {
        return buffer;
    }

}
