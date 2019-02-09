package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.keys.NetworkKeyEvent;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.world.Race;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;
import com.battlezone.megamachines.world.track.generator.TrackLoopMutation;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Game implements Runnable {

    private final NewServer server;
    private final Track track;
    private final Race race;
    private final List<RWDCar> cars;
    private boolean running = true;
    private final Map<InetAddress, Player> players;
    private final Queue<NetworkKeyEvent> inputs = new ConcurrentLinkedQueue<>();

    public Game(Map<InetAddress, Player> players, NewServer server) {

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
        while (running) {
            while (inputs.peek() != null) {
                NetworkKeyEvent key = inputs.poll();
                players.get(key.getAddress()).getCar().setDriverPressRelease(key);
            }
//            PhysicsEngine.crank();
            server.sendGameState(players);
            try {
                Thread.sleep(14);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}