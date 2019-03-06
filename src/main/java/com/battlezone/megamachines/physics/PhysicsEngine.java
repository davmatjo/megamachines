package com.battlezone.megamachines.physics;

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
    private boolean startedCrank = false;

    /**
     * This is used to stop collisions from happening every frame.
     * By limiting the number of potential collisions a second, we avoid unreasonable collision event stacking
     */
    private int counter = 0;

    /**
     * This is used to limit the number of possible collisions events that can happen
     */
    private HashMap<Pair<Collidable, Collidable>, Integer> lastCollision = new HashMap<>();

    /**
     * The list of collidable events in the physics engine
     */
    private List<Collidable> collidables = new ArrayList<>();

    /**
     * The list of cars
     */
    private ArrayList<RWDCar> cars = new ArrayList<RWDCar>();

    /**
     * This variable holds the length of the last time stamp
     */
    private double lengthOfTimestamp;


    /**
     * This method updates the state of the com.battlezone.megamachines.physics engine.
     * Preferably, it should be called at least once between each frame.
     * @param l The length of the last time stamp
     */
    public void crank(double l) {
        counter++;


        if (startedCrank) {
            return;
        }
        startedCrank = true;

        for (int i = 0; i < cars.size(); i++) {
            RWDCar car = cars.get(i);
            car.physicsStep(l);

            car.setX(car.getX() + car.getSpeed() * l * Math.cos(Math.toRadians(car.getSpeedAngle())));
            car.setY(car.getY() + car.getSpeed() * l * Math.sin(Math.toRadians(car.getSpeedAngle())));
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
                    if (lastCollision.getOrDefault(new Pair<Collidable, Collidable>(collidables.get(i), collidables.get(j)), counter - 200) + 100 < counter) {
                        Pair<Pair<Double, Double>, Pair<Double, Double>> r = Collisions.objectsCollided(collidables.get(i).getCornersOfAllHitBoxes(), collidables.get(j).getCornersOfAllHitBoxes(), collidables.get(i).getRotation());
                        collidables.get(i).collided(r.getFirst().getFirst(), r.getFirst().getSecond(), collidables.get(j), r.getSecond(), l);
                    }
                }

            }

        }

        startedCrank = false;
    }

    /**
     * Adds a new car to the physics engine
     * @param car The car to be added
     */
    public void addCar(RWDCar car) {
        cars.add(car);
        collidables.add(car);
    }

    /**
     * Removes a car from the physics engine
     * @param car The car to be removed
     */
    public void removeCar(RWDCar car) {
        cars.remove(car);
        collidables.remove(car);
    }

    /**
     * Adds a new collidable game object
     *
     * @param c The game object
     */
    public void addCollidable(Collidable c) {
        collidables.add(c);
    }

    /**
     * Removes a collidable object
     * @param c The collidable object
     */
    public void removeCollidable(Collidable c) {
        collidables.remove(c);
    }
}