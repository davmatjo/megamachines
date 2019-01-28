package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.entities.RWDCar;

import java.util.ArrayList;

/*
This is The implementation of the game's com.battlezone.megamachines.physics engine.
Here, we compute things like collision control, movement, etc.
 */
public class PhysicsEngine{
    /**
     * True if is computing current com.battlezone.megamachines.physics, false otherwise
     */
    private static boolean startedCrank = false;

    /**
     * The list of cars
     */
    private static ArrayList<RWDCar> cars = new ArrayList<RWDCar>();

    /*
    This variable stores the time at which the last crank was performed
     */
    private static double lastCrank = -1;

    /**
     * This variable holds the length of the last time stamp
     */
    private static double lengthOfTimestamp;

    /**
     * Gets the length of the last time stamp
     * @return The length of the last time stamp
     */
    public static double getLengthOfTimestamp() {
        return lengthOfTimestamp;
    }

    /*
    This method updates the state of the com.battlezone.megamachines.physics engine.
    Preferably, it should be called at least once between each frame.
     */
    public static void crank() {
        if (startedCrank) {
            return;
        }
        startedCrank = true;

        if (lastCrank == -1) {
            lastCrank = System.currentTimeMillis();
            startedCrank = false;
            return;
        }

        lengthOfTimestamp = (System.currentTimeMillis() - lastCrank) / 1000;
        lastCrank = System.currentTimeMillis();

        for (RWDCar car : cars) {
            car.physicsStep();


            car.setX(car.getX() + car.getSpeed() * lengthOfTimestamp * Math.cos(Math.toRadians(car.getAngle())));
            car.setY(car.getY() + car.getSpeed() * lengthOfTimestamp * Math.sin(Math.toRadians(car.getAngle())));
        }

        startedCrank = false;
    }

    /**
     * Adds a new car
     * @param car
     */
    public static void addCar(RWDCar car) {
        cars.add(car);
    }
}