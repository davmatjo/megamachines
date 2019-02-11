package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.ai.Driver;
import com.battlezone.megamachines.ai.TrackRoute;
import com.battlezone.megamachines.entities.Cars.DordConcentrate;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.keys.NetworkKeyEvent;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.world.Race;
import com.battlezone.megamachines.world.ScaleController;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;
import com.battlezone.megamachines.world.track.generator.TrackLoopMutation;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Game implements Runnable {

    private static final double TARGET_FPS = 60.0;
    private static final double FRAME_LENGTH = 1000000000 / TARGET_FPS;
    private final NewServer server;
    private final Track track;
    private final Race race;
    private final List<Driver> AIs;
    private final List<RWDCar> cars;
    private boolean running = true;
    private final Map<InetAddress, Player> players;
    private final Queue<NetworkKeyEvent> inputs = new ConcurrentLinkedQueue<>();

    public Game(Map<InetAddress, Player> players, NewServer server, int aiCount) {

        track = new TrackLoopMutation(10,10).generateTrack();
        track.printTrack();
        cars = new ArrayList<>();
        TrackPiece startPiece = track.getStartPiece();
        players.forEach(((address, player) -> {
            RWDCar car = player.getCar();
            car.setX(startPiece.getX());
            car.setY(startPiece.getY());
            cars.add(car);
            PhysicsEngine.addCar(player.getCar());
        }));

        Random r = new Random();
        this.AIs = new ArrayList<>() {{
            TrackRoute route = new TrackRoute(track);
            for (int i=0; i<aiCount; i++) {

                RWDCar ai = new DordConcentrate(
                        track.getStartPiece().getX() + 2 + i*1.5,
                        track.getStartPiece().getY(),
                        ScaleController.RWDCAR_SCALE,
                        1 + r.nextInt(2),
                        new Vector3f(r.nextFloat(), r.nextFloat(), r.nextFloat()));
                cars.add(ai);
                add(new Driver(route, ai));
                PhysicsEngine.addCar(ai);
            }
        }};

        track.getEdges().forEach(PhysicsEngine::addCollidable);
        race = new Race(track, 2, cars);
        this.players = players;
        this.server = server;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public Track getTrack() {
        return track;
    }

    public void keyPress(NetworkKeyEvent event) {
        inputs.add(event);
    }

    @Override
    public void run() {
        double previousTime = System.nanoTime();
        double currentTime;
        double interval;
        try {Thread.sleep(14);} catch (InterruptedException ignored) {};

        while (running) {
            while (inputs.peek() != null) {
                NetworkKeyEvent key = inputs.poll();
                players.get(key.getAddress()).getCar().setDriverPressRelease(key);
            }

            currentTime = System.nanoTime();
            interval = currentTime - previousTime;
            previousTime = currentTime;

            for (int i=0; i<AIs.size(); i++) {
                AIs.get(i).update();
            }

            PhysicsEngine.crank(interval / 1000000);
            server.sendGameState(players, cars);
            while (System.nanoTime() - previousTime < FRAME_LENGTH) {
                try {Thread.sleep(0);} catch (InterruptedException ignored) {}
            }
        }
    }

    public List<RWDCar> getCars() {
        return cars;
    }
}