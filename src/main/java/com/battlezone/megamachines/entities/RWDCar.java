package com.battlezone.megamachines.entities;

import com.battlezone.megamachines.events.keys.KeyPressEvent;
import com.battlezone.megamachines.events.keys.KeyRepeatEvent;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.entities.abstractCarComponents.*;

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
     * A number between 0 and 1 which expresses how much the accelerator pedal was pressed
     */
    public double accelerationAmount = 0;

    /**
     * A number between 0 and 1 which expresses how much the brake pedal was pressed
     */
    public double brakeAmount = 0;

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
     * This method should be called once per com.battlezone.megamachines.physics step
     */
    public void physicsStep() {
        accelerationAmount = 0.7;
        this.engine.pushTorque(accelerationAmount);

        flWheel.brake(brakeAmount);
        frWheel.brake(brakeAmount);
        blWheel.brake(brakeAmount);
        brWheel.brake(brakeAmount);

        flWheel.physicsStep();
        frWheel.physicsStep();
        blWheel.physicsStep();
        brWheel.physicsStep();

        this.engine.getNewRPM();
    }

    public int getModelNumber() {
        return modelNumber;
    }

    @EventListener
    public void setAccelerationWasPressed(KeyPressEvent event) {
        if (event.getKeyCode() == 87) {
            //TODO: make this a linear movement
            accelerationAmount = 1;
        }
    }

    @EventListener
    public void setAccelerationWasContinued(KeyRepeatEvent event) {
        if (event.getKeyCode() == 87) {
            //TODO: make this a linear movement
            accelerationAmount = 1;
        }
    }

}
