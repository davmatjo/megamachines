package com.battlezone.megamachines.world;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.world.track.Track;

import java.util.List;

public class SingleplayerWorld extends BaseWorld {

    private final Race race;

    public SingleplayerWorld(List<RWDCar> cars, Track track, int playerNumber, int aiCount) {
        super(cars, track, playerNumber, aiCount);
        cars.forEach(PhysicsEngine::addCar);
        this.race = new Race(track, 3, cars);
    }

    @Override
    void preRender(double interval) {
        PhysicsEngine.crank(interval / 1000000);
        race.update();
    }


}
