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
     * The car's body
     */
    private CarBody carBody;
    /**
     * The car's back differential
     */
    private Differential backDifferential;
    /**
     * The car's drive shaft
     */
    private DriveShaft driveShaft;
    /**
     * The car's engine
     */
    private Engine engine;
    /**
     * The car's gearbox
     */
    private Gearbox gearbox;
    /**
     * The car's springs
     */
    private Springs springs;
    /**
     * The car's front left wheel
     */
    private Wheel flWheel;
    /**
     * The car's front right wheel
     */
    private Wheel frWheel;
    /**
     * The car's back left wheel
     */
    private Wheel blWheel;
    /**
     * The car's back right wheel
     */
    private Wheel brWheel;

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
        this.carBody = carBody;
        this.backDifferential = backDifferential;
        this.driveShaft = driveShaft;
        this.engine = engine;
        this.gearbox = gearbox;
        this.springs = springs;
        this.flWheel = flWheel;
        this.frWheel = frWheel;
        this.blWheel = blWheel;
        this.brWheel = brWheel;

        this.weightOnFront = carBody.getWeight() / 2 + engine.getWeight();
        this.weightOnBack = carBody.getWeight() / 2 + engine.getWeight();
    }

    /**
     * This function tells the car to accelerate
     */
    public void accelerate() {

    }

    /**
     * TODO: Get weight on a per wheel basis
     */
    public double getLoadOnWheel() {
        return (this.carBody.getWeight() + this.engine.getWeight()) / 4;
    }
}
