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
     * True when the acceleration was pressed from lass physics call, false otherwise
     */
    public boolean accelerationWasPressed = false;

    /**
     * The amount of weight the car has on the back wheels when stationary
     */
    private double weightOnBack;

    /**
     * The car's body
     */
    protected CarBody carBody;
    /**
     * The car's back differential
     */
    protected Differential backDifferential;
    /**
     * The car's drive shaft
     */
    protected DriveShaft driveShaft;
    /**
     * The car's engine
     */
    protected Engine engine;
    /**
     * The car's gearbox
     */
    protected Gearbox gearbox;
    /**
     * The car's springs
     */
    protected Springs springs;
    /**
     * The car's front left wheel
     */
    protected Wheel flWheel;
    /**
     * The car's front right wheel
     */
    protected Wheel frWheel;
    /**
     * The car's back left wheel
     */
    protected Wheel blWheel;
    /**
     * The car's back right wheel
     */
    protected Wheel brWheel;

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
                  Gearbox gearbox, Wheel flWheel, Wheel frWheel, Wheel blWheel, Wheel brWheel) {
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

    public RWDCar(){

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

    /**
     * This method should be called once per physics step
     */
    public void physicsStep() {
        if (accelerationWasPressed) {
            this.engine.pushTorque();
        }

        flWheel.physicsStep();
        frWheel.physicsStep();
        blWheel.physicsStep();
        brWheel.physicsStep();

        this.engine.getNewRPM();

        accelerationWasPressed = false;
    }
}
