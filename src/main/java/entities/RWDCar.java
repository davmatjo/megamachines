package entities;

import entities.abstractCarComponents.*;

/**
 * This is a Rear Wheel Drive car
 */
public class RWDCar extends PhysicalEntity {
    /**
     * The amount of weight the car has on the front wheels when stationary
     */
    private double weightOnFront;

    /**
     * The amount of weight the car has on the back wheels when stationary
     */
    private double weightOnBack;

    /**
     * The constructor for a Rear Wheel Drive car
     * @param carBody The car body
     * @param backDifferential The back differential
     * @param driveShaft The driveShaft
     * @param engine The engine
     * @param gearbox The gearbox
     * @param springs The springs
     * @param flWheel The front left wheel
     * @param frWheel The front right wheel
     * @param blWheel The back left wheel
     * @param brWheel The back right wheel
     */
    public RWDCar(CarBody carBody, Differential backDifferential, DriveShaft driveShaft, Engine engine,
                  Gearbox gearbox, Springs springs, Wheel flWheel, Wheel frWheel, Wheel blWheel, Wheel brWheel) {
        this.addComponent(carBody);
        this.addComponent(backDifferential);
        this.addComponent(driveShaft);
        this.addComponent(engine);
        this.addComponent(gearbox);
        this.addComponent(springs);
        this.addComponent(flWheel);
        this.addComponent(frWheel);
        this.addComponent(blWheel);
        this.addComponent(brWheel);
    }

    /**
     * This function tells the car to accelerate
     */
    public void accelerate() {

    }
}
