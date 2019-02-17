package com.battlezone.megamachines.world;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.world.track.Track;

import java.util.List;

public class SingleplayerWorld extends BaseWorld {

    private final Race race;
    private final PhysicsEngine physicsEngine;

    public SingleplayerWorld(List<RWDCar> cars, Track track, int playerNumber, int aiCount) {
        super(cars, track, playerNumber, aiCount);
        this.physicsEngine = new PhysicsEngine();
        List<Vector3f> startPositions = track.getStartingPositions();
        for (int i=0; i<cars.size(); i++) {
            cars.get(i).setX(startPositions.get(i).x);
            cars.get(i).setY(startPositions.get(i).y);
            cars.get(i).setAngle(startPositions.get(i).z);
        }
        cars.forEach(physicsEngine::addCar);
        this.race = new Race(track, 3, cars);
    }

    @Override
    void preRender(double interval) {
        physicsEngine.crank(interval / 1000000000);
        race.update();
    }

    @Override
    void preLoop() {
        for (int i=3; i>=0; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MessageBus.fire(new ErrorEvent("GET READY", i == 0 ? "GO" : Integer.toString(i), 1));
        }
    }
}