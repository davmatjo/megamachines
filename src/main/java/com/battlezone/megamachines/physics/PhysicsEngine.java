package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.NewMain;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
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

    private static int counter = 0;

    private static HashMap<Pair<Collidable, Collidable>, Integer> lastCollision = new HashMap<>();

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
        counter++;

        lengthOfTimestamp = l / 1000;

        if (startedCrank) {
            return;
        }
        startedCrank = true;

        for (RWDCar car : cars) {
            car.physicsStep();

            car.setX(car.getX() + car.getSpeed() * PhysicsEngine.getLengthOfTimestamp() * Math.cos(Math.toRadians(car.getSpeedAngle())));
            car.setY(car.getY() + car.getSpeed() * PhysicsEngine.getLengthOfTimestamp() * Math.sin(Math.toRadians(car.getSpeedAngle())));
        }

        for (int i = 0; i < collidables.size(); i++) {
            for (int j = 0; j < collidables.size(); j++) {
//                if (Collisions.objectsCollided(collidables.get(i).getCornersOfAllHitBoxes(), collidables.get(j).getCornersOfAllHitBoxes()) != null) {
//                    if (LineCollisions.objectsCollided(collidables.get(i), collidables.get(j)) != null) {
//                        Pair<Pair<Double, Double>, Pair<Double, Double>> collisionPoint = LineCollisions.objectsCollided(collidables.get(i), collidables.get(j));
//                        collidables.get(i).collided(collisionPoint.getFirst().getFirst(), collisionPoint.getFirst().getSecond(), collidables.get(j), collisionPoint.getSecond());
//                    }
//                    if (LineCollisions.objectsCollided(collidables.get(j), collidables.get(i)) != null) {
//                        Pair<Pair<Double, Double>, Pair<Double, Double>> collisionPoint = LineCollisions.objectsCollided(collidables.get(j), collidables.get(i));
//                        collidables.get(j).collided(collisionPoint.getFirst().getFirst(), collisionPoint.getFirst().getSecond(), collidables.get(i), collisionPoint.getSecond());
//                    }
//                }
                if (Collisions.objectsCollided(collidables.get(i).getCornersOfAllHitBoxes(), collidables.get(j).getCornersOfAllHitBoxes(), collidables.get(i).getRotation()) != null &&
                        i != j) {
                    if (counter - lastCollision.getOrDefault(new Pair<>(collidables.get(i), collidables.get(j)), counter - 21) > 5) {
                        Pair<Pair<Double, Double>, Pair<Double, Double>> r = Collisions.objectsCollided(collidables.get(i).getCornersOfAllHitBoxes(), collidables.get(j).getCornersOfAllHitBoxes(), collidables.get(i).getRotation());
                        collidables.get(i).collided(r.getFirst().getFirst(), r.getFirst().getSecond(), collidables.get(j), r.getSecond());
                        lastCollision.put(new Pair<>(collidables.get(i), collidables.get(j)), counter);
                    }
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