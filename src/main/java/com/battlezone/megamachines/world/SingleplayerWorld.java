package com.battlezone.megamachines.world;

import com.battlezone.megamachines.ai.Driver;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.track.Track;

import java.util.ArrayList;
import java.util.List;

public class SingleplayerWorld extends BaseWorld {

    private final Race race;
    private final List<Driver> AIs;

    public SingleplayerWorld(List<RWDCar> cars, Track track, int playerNumber, int aiCount, int lapCount) {
        super(cars, track, playerNumber, aiCount, lapCount);
        List<Vector3f> startPositions = track.getStartingPositions();
        for (int i = 0; i < cars.size(); i++) {
            cars.get(i).setX(startPositions.get(i).x);
            cars.get(i).setY(startPositions.get(i).y);
            cars.get(i).setAngle(startPositions.get(i).z);
        }
        this.race = new Race(track, lapCount, cars);
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
            AIs.get(i).update(interval);
        }

        Pair<RWDCar, Byte> recentFinish = race.getRecentlyFinished();
        if (recentFinish != null) {
            if (recentFinish.getFirst() == target) {
                MessageBus.fire(new ErrorEvent("YOU FINISHED", Race.positions[recentFinish.getSecond()], 2, Colour.GREEN));
            } else {
                MessageBus.fire(new ErrorEvent(cars.get(cars.indexOf(recentFinish.getFirst())).getName() + " FINISHED", Race.positions[recentFinish.getSecond()], 2, Colour.GREEN));
            }
        }

        if (race.hasFinished()) {
            setRunning(false);
        }
    }

    @Override
    boolean canPause() {
        return true;
    }
}
