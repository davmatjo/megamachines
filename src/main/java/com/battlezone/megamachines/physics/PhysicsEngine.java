package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.NewMain;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.util.Pair;

import java.util.ArrayList;
import java.util.List;

/*
This is The implementation of the game's com.battlezone.megamachines.physics engine.
Here, we compute things like collision control, movement, etc.
 */
public class PhysicsEngine {
    /**
     * True if is computing current com.battlezone.megamachines.physics, false otherwise
     */
    private static boolean startedCrank = false;

    private static List<Collidable> collidables = new ArrayList<>();

    /**
     * The list of cars
     */
    private static ArrayList<RWDCar> cars = new ArrayList<RWDCar>();

    /**
     * This variable holds the length of the last time stamp
     */
    private static double lengthOfTimestamp;

    /**
     * Gets the length of the last time stamp
     *
     * @return The length of the last time stamp
     */
    public static double getLengthOfTimestamp() {
        return lengthOfTimestamp;
    }

    /**
     * This method updates the state of the com.battlezone.megamachines.physics engine.
     * Preferably, it should be called at least once between each frame.
     * @param l The length of the last time stamp
     */
    public static void crank(double l) {
        lengthOfTimestamp = l / 1000;

        if (startedCrank) {
            return;
        }
        startedCrank = true;

        for (RWDCar car : cars) {
            car.physicsStep();

            double oldX = car.getX();
            double oldY = car.getY();

            car.setX(car.getX() + car.getSpeed() * PhysicsEngine.getLengthOfTimestamp() * Math.cos(Math.toRadians(car.getSpeedAngle())));
            car.setY(car.getY() + car.getSpeed() * PhysicsEngine.getLengthOfTimestamp() * Math.sin(Math.toRadians(car.getSpeedAngle())));

            car.positionDelta.setFirst(car.getX() - oldX);
            car.positionDelta.setSecond(car.getY() - oldY);
        }

        for (int i = 0; i < collidables.size(); i++) {
            for (int j = i + 1; j < collidables.size(); j++) {
                if (LineCollisions.objectsCollided(collidables.get(i), collidables.get(j)) != null) {
                    Pair<Pair<Double, Double>, Pair<Double, Double>> collisionPoint = LineCollisions.objectsCollided(collidables.get(i), collidables.get(j));
                    collidables.get(i).collided(collisionPoint.getFirst().getFirst(), collisionPoint.getFirst().getSecond(), collidables.get(j), collisionPoint.getSecond());
                }
            }
        }

        startedCrank = false;
    }

    /**
     * Adds a new car
     *
     * @param car
     */
    public static void addCar(RWDCar car) {
        cars.add(car);
        collidables.add(car);
    }

    /**
     * Adds a new collidable game object
     *
     * @param o The game object
     */
    public static void addCollidable(Collidable c) {
        collidables.add(c);
    }
}