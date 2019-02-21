package com.battlezone.megamachines.networking.server.game;

import com.battlezone.megamachines.ai.Driver;
import com.battlezone.megamachines.entities.Cars.AffordThoroughbred;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.keys.NetworkKeyEvent;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.game.animation.Animatable;
import com.battlezone.megamachines.world.Race;
import com.battlezone.megamachines.world.ScaleController;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.generator.TrackCircleLoop;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Game implements Runnable {

    private static final double TARGET_FPS = 60.0;
    private static final double FRAME_TIME = 1.0/60.0;
    private static final double FRAME_LENGTH = 1000000000 / TARGET_FPS;
    private final GameRoom gameRoom;
    private final Track track;
    private final Race race;
    private final List<Driver> AIs;
    private final List<RWDCar> cars;
    private boolean running = true;
    private final Queue<NetworkKeyEvent> inputs = new ConcurrentLinkedQueue<>();
    private final Queue<RWDCar> lostPlayers = new ConcurrentLinkedQueue<>();
    private final PhysicsEngine physicsEngine;
    private final List<Animatable> animatables;

    public Game(List<RWDCar> cars, GameRoom gameRoom, int aiCount) {

        this.physicsEngine = new PhysicsEngine();
        this.track = new TrackCircleLoop(10, 10, false).generateTrack();
        System.out.println(track);
        List<Vector3f> startingGrid = track.getStartingPositions();

        this.cars = cars;
        this.animatables = new ArrayList<>();

        Random r = new Random();
        for (int i = 0; i < aiCount; i++) {

            RWDCar ai = new AffordThoroughbred(
                    track.getFinishPiece().getX() + 2 + i * 2,
                    track.getFinishPiece().getY(),
                    ScaleController.RWDCAR_SCALE,
                    1 + r.nextInt(2),
                    new Vector3f(r.nextFloat(), r.nextFloat(), r.nextFloat()), 0, 1);
            cars.add(ai);

        }

        int i = cars.size() - 1;
        for (RWDCar car : cars) {
            car.setX(startingGrid.get(i).x);
            car.setY(startingGrid.get(i).y);
            car.setAngle(startingGrid.get(i).z);
            physicsEngine.addCar(car);
            animatables.add(car);
            i--;
        }

        race = new Race(track, 1, cars);
        this.AIs = new ArrayList<>() {{
            for (int i=cars.size() - 1; i >= cars.size() - aiCount; i--) {
                add(new Driver(track, cars.get(i), race));
            }
        }};

        this.gameRoom = gameRoom;
    }

    public Track getTrack() {
        return track;
    }

    public void keyPress(NetworkKeyEvent event) {
//        System.out.println(event.getKeyCode());
        inputs.add(event);
    }

    public void close() {
        this.running = false;
    }

    @Override
    public void run() {

        double previousTime = System.nanoTime();
        double frametime = 0;
        int frames = 0;

        runCountdown(previousTime);

        previousTime = System.nanoTime();

        while (running) {
            physicsEngine.crank(FRAME_TIME);

            double currentTime = System.nanoTime();
            double interval = currentTime - previousTime;
            frametime += interval;
            frames += 1;
            previousTime = currentTime;

            while (!inputs.isEmpty()) {
                NetworkKeyEvent key = inputs.poll();
                key.getPlayer().setDriverPressRelease(key);
            }

            if (!lostPlayers.isEmpty()) {
                physicsEngine.removeCar(lostPlayers.poll());
            }

            for (int i = 0; i < animatables.size(); i++) {
                animatables.get(i).animate(interval / 1000000000);
            }

            for (int i = 0; i < AIs.size(); i++) {
                AIs.get(i).update();
            }
            gameRoom.sendGameState(cars);
            race.update();

            if (frametime >= 1000000000) {
                frametime = 0;
                System.out.println("UPS: " + frames);
                frames = 0;
            }

            while (System.nanoTime() - previousTime < FRAME_LENGTH) {
                try {
                    Thread.sleep(0);
                } catch (InterruptedException ignored) {
                }
            }
            if (race.hasFinished()) {
                running = false;
                System.out.println(race.getFinalPositions());
                System.out.println();
                System.out.println(cars);
                gameRoom.end(race.getFinalPositions(), cars);
            }
        }
        System.out.println("Game ending");
    }

    private void runCountdown(double previousTime) {
        for (int i=3; i>=0; i--) {
            while (System.nanoTime() - previousTime < FRAME_LENGTH * TARGET_FPS) {
                try {
                    Thread.sleep(30);
                    gameRoom.sendGameState(cars);
                } catch (InterruptedException ignored) {
                }
            }
            previousTime = System.nanoTime();
            gameRoom.sendCountDown(i);
        }
    }

    public void removePlayer(RWDCar car) {
        lostPlayers.add(car);
    }

    public List<RWDCar> getCars() {
        return cars;
    }
}