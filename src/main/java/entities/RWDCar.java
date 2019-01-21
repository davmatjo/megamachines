package entities;

import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.events.keys.KeyPressEvent;
import com.battlezone.megamachines.events.keys.KeyRepeatEvent;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import entities.abstractCarComponents.*;

/**
 * This is a Rear Wheel Drive car
 */
public class RWDCar extends PhysicalEntity {
    /**
     * The amount of weight the car has on the front wheels when stationary
     */
    private double weightOnFront;

    private final int modelNumber;

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

    public double getWeight() {
        return carBody.getWeight();
    }

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
//    public RWDCar(CarBody carBody, Differential backDifferential, DriveShaft driveShaft, Engine engine,
//                  Gearbox gearbox, Wheel flWheel, Wheel frWheel, Wheel blWheel, Wheel brWheel, int modelNumber) {
//        super();
//        MessageBus.register(this);
//        this.modelNumber = modelNumber;
//        this.carBody = carBody;
//        this.backDifferential = backDifferential;
//        this.driveShaft = driveShaft;
//        this.engine = engine;
//        this.gearbox = gearbox;
//        this.springs = springs;
//        this.flWheel = flWheel;
//        this.frWheel = frWheel;
//        this.blWheel = blWheel;
//        this.brWheel = brWheel;
//
//        this.weightOnFront = carBody.getWeight() / 2 + engine.getWeight();
//        this.weightOnBack = carBody.getWeight() / 2 + engine.getWeight();
//    }

    public RWDCar(double x, double y, float scale, int modelNumber){
        super(x, y, scale);
        MessageBus.register(this);
        this.modelNumber = modelNumber;
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
            System.out.println("Pressed");
            System.out.println("AAA" + this.gearbox.currentGear);
        } else {
            System.out.println("Not pressed");
        }

        flWheel.physicsStep();
        frWheel.physicsStep();
        blWheel.physicsStep();
        brWheel.physicsStep();

        this.engine.getNewRPM();

        accelerationWasPressed = false;
    }

    public int getModelNumber() {
        return modelNumber;
    }

    @EventListener
    public void setAccelerationWasPressed(KeyPressEvent event) {
        if (event.getKeyCode() == 87) {
            accelerationWasPressed = true;
        }
    }

    @EventListener
    public void setAccelerationWasContinued(KeyRepeatEvent event) {
        if (event.getKeyCode() == 87) {
            accelerationWasPressed = true;
        }
    }
}
