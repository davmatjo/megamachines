package physics;

import entities.RWDCar;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;

/*
This is The implementation of the game's physics engine.
Here, we compute things like collision control, movement, etc.
 */
public class PhysicsEngine{

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
    This method updates the state of the physics engine.
    Preferably, it should be called at least once between each frame.
     */
    public static void crank() {
        if (lastCrank == -1) {
            lastCrank = System.currentTimeMillis();
            return;
        }

        lengthOfTimestamp = System.currentTimeMillis() - lastCrank;
        lastCrank = System.currentTimeMillis();

        for (RWDCar car : cars) {
            car.physicsStep();
            car.setX(car.getX() + car.getSpeed() * lengthOfTimestamp);
        }
    }

    public void addCar(RWDCar car) {
        cars.add(car);
    }
}