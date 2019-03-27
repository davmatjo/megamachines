package com.battlezone.megamachines.world;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.entities.powerups.types.*;
import com.battlezone.megamachines.events.game.GameUpdateEvent;
import com.battlezone.megamachines.events.game.PowerupTriggerEvent;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.networking.server.Server;
import com.battlezone.megamachines.renderer.game.animation.Animation;
import com.battlezone.megamachines.world.track.Track;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiplayerWorld extends BaseWorld {

    private static boolean isActive = false;
    private final Queue<GameUpdateEvent> gameUpdates;
    private final Queue<PowerupTriggerEvent> powerupEvents;
    private Map<Byte, Powerup> idToPowerup;
    private byte lapCounter;

    public MultiplayerWorld(List<RWDCar> cars, Track track, int playerNumber, int aiCount, byte[] manager, int lapCount) {
        super(cars, track, playerNumber, aiCount, lapCount);
        isActive = true;
        this.gameUpdates = new ConcurrentLinkedQueue<>();
        this.powerupEvents = new ConcurrentLinkedQueue<>();
        this.manager = PowerupManager.fromByteArray(manager, physicsEngine, renderer);
        renderer.addDrawable(this.manager);
        initPowerupMap();
    }

    public static boolean isActive() {
        return isActive;
    }

    public static void setActive(boolean isActive) {
        MultiplayerWorld.isActive = isActive;
    }

    // Creates the map where all power ups are stored to have corresponding id for each type
    private void initPowerupMap() {
        this.idToPowerup = new HashMap<>();

        Agility agility = new Agility(manager, physicsEngine, renderer);
        Bomb bomb = new Bomb(manager, physicsEngine, renderer);
        FakeItem item = new FakeItem(manager, physicsEngine, renderer);
        Growth grow = new Growth(manager, physicsEngine, renderer);
        OilSpill oil = new OilSpill(manager, physicsEngine, renderer);

        idToPowerup.put(Agility.id, agility);
        idToPowerup.put(Bomb.id, bomb);
        idToPowerup.put(FakeItem.id, item);
        idToPowerup.put(Growth.id, grow);
        idToPowerup.put(OilSpill.id, oil);
    }

    @Override
    void preRender(double interval) {

//        System.out.println(gameUpdates.size());
        if (!gameUpdates.isEmpty()) {
            update(gameUpdates.poll());
        }
        while (gameUpdates.size() > 3) {
            update(gameUpdates.poll());
        }

        while (!powerupEvents.isEmpty()) {
            var update = powerupEvents.poll();
            var updateArr = update.getData();
            var player = cars.get(updateArr[1]);
            var powerupType = Powerup.POWERUP_MAP.get(updateArr[2]);
            if (powerupType != null) {
                manager.pickedUp(Powerup.POWERUP_MAP.get(updateArr[2]), player);
                player.getCurrentPowerup().activate();
            } else {
                System.err.println("tried to use null powerup");
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
            var powerupType = Powerup.POWERUP_MAP.get(buffer.get(i + 100));
            manager.pickedUp(powerupType, player);
//            player.setCurrentPowerup(idToPowerup.get(buffer.get(i + 100)));

//            System.out.println(buffer.get(i + 100));
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

    @EventListener
    public void receivePowerupEvents(PowerupTriggerEvent powerupTriggerEvent) {
        powerupEvents.add(powerupTriggerEvent);
    }

    @Override
    void preLoop() {

    }

    @Override
    boolean canPause() {
        return true;
    }

}
