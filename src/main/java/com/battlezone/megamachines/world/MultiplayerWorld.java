package com.battlezone.megamachines.world;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.game.GameUpdateEvent;
import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.networking.server.Server;
import com.battlezone.megamachines.renderer.game.animation.Animation;
import com.battlezone.megamachines.world.track.Track;

import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class MultiplayerWorld extends BaseWorld {

    private final Queue<GameUpdateEvent> gameUpdates;

    private int packetBufferSize = 0;
    private int stablePacketCount;
    private boolean latencyTrip = false;

    public MultiplayerWorld(List<RWDCar> cars, Track track, int playerNumber, int aiCount) {
        super(cars, track, playerNumber, aiCount);
        this.gameUpdates = new ConcurrentLinkedQueue<>();
    }

    @Override
    void preRender(double interval) {

        if (!gameUpdates.isEmpty()) {
            update(gameUpdates.poll());
        } else {
            packetBufferSize++;
            stablePacketCount = 0;
            if (packetBufferSize > 4 && !latencyTrip) {
                latencyTrip = true;
                MessageBus.fire(new ErrorEvent("NETWORK ERROR", "HIGH LATENCY VARIATION", 1));
            }
        }
        if (gameUpdates.size() == packetBufferSize) {
            stablePacketCount++;
            if (stablePacketCount > 60) {
                packetBufferSize = Math.max(0, packetBufferSize - 1);
                if (packetBufferSize < 3) {
                    latencyTrip = false;
                }
            }
        } else {
            while (gameUpdates.size() > packetBufferSize) {
                update(gameUpdates.poll());
            }
        }



    }

    private void update(GameUpdateEvent update) {
        ByteBuffer buffer = update.getBuffer();
        byte playerCount = buffer.get(1);
        int playerNumber = 0;
        for (int i = 3; i < playerCount * Server.GAME_STATE_EACH_LENGTH; i += Server.GAME_STATE_EACH_LENGTH) {
            RWDCar player = cars.get(playerNumber);

            player.setX(buffer.getDouble(i));
            player.setY(buffer.getDouble(i + 8));
            player.setAngle(buffer.getDouble(i + 16));
            player.setSpeed(buffer.getDouble(i + 24));
            player.setLongitudinalWeightTransfer(buffer.getDouble(i + 32));
            player.setAngularSpeed(buffer.getDouble(i + 40));
            player.setSpeedAngle(buffer.getDouble(i + 48));
            player.getFlWheel().setAngularVelocity(buffer.getDouble(i + 56));
            player.getFrWheel().setAngularVelocity(buffer.getDouble(i + 64));
            player.getBlWheel().setAngularVelocity(buffer.getDouble(i + 72));
            player.getBrWheel().setAngularVelocity(buffer.getDouble(i + 80));
            player.getEngine().setRPM(buffer.getDouble(i + 88));
            player.getGearbox().setCurrentGear(buffer.get(i + 96));
            player.setLap(buffer.get(i + 97));
            player.setPosition(buffer.get(i + 98));

            if (buffer.get(i + 99) != 0) {
                player.playAnimation(Animation.INDEX_TO_ANIM.get(buffer.get(i + 99)));
            }

            playerNumber++;
        }

        update.delete();
    }

    @EventListener
    public void receiveGameUpdates(GameUpdateEvent gameUpdate) {
        gameUpdates.add(gameUpdate);
    }

    @Override
    void preLoop() {

    }

    @Override
    boolean canPause() {
        return true;
    }

}
