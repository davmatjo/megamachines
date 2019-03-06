package com.battlezone.megamachines.world;

import com.battlezone.megamachines.ai.Driver;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.world.track.Track;

import java.util.ArrayList;
import java.util.List;

public class SingleplayerWorld extends BaseWorld {

    private final Race race;
    private final List<Driver> AIs;

    public SingleplayerWorld(List<RWDCar> cars, Track track, int playerNumber, int aiCount) {
        super(cars, track, playerNumber, aiCount);
        List<Vector3f> startPositions = track.getStartingPositions();
        for (int i = 0; i < cars.size(); i++) {
            cars.get(i).setX(startPositions.get(i).x);
            cars.get(i).setY(startPositions.get(i).y);
            cars.get(i).setAngle(startPositions.get(i).z);
        }
        this.race = new Race(track, 3, cars);
        this.AIs = new ArrayList<>() {{
            for (int i = cars.size() - 1; i >= cars.size() - aiCount; i--) {
                add(new Driver(track, cars.get(i), race));
            }
        }};

        this.manager = new PowerupManager(track, physicsEngine, renderer);
        renderer.addDrawable(this.manager);
        this.manager.initSpaces();
    }

    @Override
    void preRender(double interval) {
        race.update();

        for (int i = 0; i < AIs.size(); i++) {
            AIs.get(i).update();
        }

        if (race.hasFinished()) {
            setRunning(false);
        }
    }

    @Override
    void preLoop() {
        /*for (int i=3; i>=0; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MessageBus.fire(new ErrorEvent("GET READY", i == 0 ? "GO" : Integer.toString(i), 1));
        }*/
    }

    @Override
    boolean canPause() {
        return true;
    }
}
