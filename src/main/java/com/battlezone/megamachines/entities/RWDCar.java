package com.battlezone.megamachines.entities;

import com.battlezone.megamachines.Main;
import com.battlezone.megamachines.entities.abstractCarComponents.*;
import com.battlezone.megamachines.events.keys.KeyPressEvent;
import com.battlezone.megamachines.events.keys.KeyReleaseEvent;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.physics.Collidable;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.util.Pair;

import static org.lwjgl.opengl.GL11.*;

/**
 * This is a Rear Wheel Drive car
 */
public abstract class RWDCar extends PhysicalEntity implements Drawable, Collidable {
    private final int indexCount;

    /**
     * The wheelbase of a car is defined as the distance between
     * The front and the back wheels
     */
    protected double wheelBase;

    /**
     * The drag coefficient is used to compute the amount of drag the car experiences when moving
     */
    protected double dragCoefficient;

    /**
     * The steering angle of this car
     */
    protected double steeringAngle = 0;

    /**
     * The car's maximum steering angle.
     * This is defined as the maximum angle each front wheel can turn
     */
    protected double maximumSteeringAngle;

    /**
     * The shader used for this car
     */
    private static final Shader SHADER = AssetManager.loadShader("/shaders/car");

    /**
     * This car's model number
     */
    private final int modelNumber;

    /**
     * A temporary matrix used for computing the corners of the car
     */
    private Matrix4f tempMatrix = new Matrix4f();

    /**
     * The car's color
     */
    private final Vector3f colour;

    /**
     * The car's texture
     */
    private final Texture texture;

    /**
     * A number between 0 and 1 which expresses how much the accelerator pedal was pressed
     */
    public double accelerationAmount = 0;

    /**
     * A number between 0 and 1 which expresses how much the brake pedal was pressed
     */
    public double brakeAmount = 0;

    /**
     * The amount the steering wheel is turned to the left
     */
    public double turnAmount;

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

    private final Model model;

    /**
     * Adds a force vector (over the timie from the last physcs step) to the speed vector
     * @param force The force to be applied
     * @param angle The absolute angle of the force
     */
    public void addForce(Double force, double angle){
        force *= PhysicsEngine.getLengthOfTimestamp();
        force /= this.getWeight();

        double x = getSpeed() * Math.cos(Math.toRadians(speedAngle)) + force * Math.cos(Math.toRadians(angle));
        double y = getSpeed() * Math.sin(Math.toRadians(speedAngle)) + force * Math.sin(Math.toRadians(angle));

        setSpeed(Math.sqrt((Math.pow(x, 2) + Math.pow(y, 2))));
        speedAngle = Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the car's weight
     *
     * @return The car's weight
     */
    public double getWeight() {
        return carBody.getWeight();
    }

    /**
     * Returns the engine of the car
     *
     * @return The engine of the car
     */
    public Engine getEngine() {
        return this.engine;
    }

    /**
     * Returns the gearbox of the car
     *
     * @return The gearbox of the car
     */
    public Gearbox getGearbox() {
        return this.gearbox;
    }

    public RWDCar(double x, double y, float scale, int modelNumber, Vector3f colour) {
        super(x, y, scale);
        MessageBus.register(this);
        this.modelNumber = modelNumber;
        this.texture = AssetManager.loadTexture("/cars/car" + modelNumber + ".png");
        this.colour = colour;
        this.model = Model.generateCar();
        this.indexCount = model.getIndices().length;
    }

    /**
     * TODO: Get weight on a per wheel basis
     */
    public double getLoadOnWheel() {
        return (this.carBody.getWeight() + this.engine.getWeight()) / 4;
    }

    //TODO: The center of weight will move in the future
    /**
     * Gets the distance from the center of weight to the rear axle
     * @return The distance from the center of weight to the rear axle
     */
    public double getDistanceCenterOfWeightRearAxle() {
        return wheelBase * (2.0 / 5.0);
    }

    /**
     * Gets the distance from the center of weight to the front axle
     * @return The distance from the center of weight to the front axle
     */
    public double getDistanceCenterOfWeightFrontAxle() {
        return wheelBase - getDistanceCenterOfWeightRearAxle();
    }

    /**
     * Determines on which of the axles the wheel sits and
     * returns the appropiate distance to the center of weight on the longitudinal axis
     * @param wheel The wheel
     * @return The longitudinal distance to the center of weight
     */
    public double getDistanceToCenterOfWeightLongitudinally(Wheel wheel) {
        if (wheel == flWheel || wheel == frWheel) {
            return getDistanceCenterOfWeightFrontAxle();
        } else {
            return getDistanceCenterOfWeightRearAxle();
        }
    }

    /**
     * Gets the longitudinal speed of the car
     * i.e. the speed with which the car is moving to where it's pointing
     * @return The longitudinal speed of the car
     */
    public double getLongitudinalSpeed() {
        return Math.cos(Math.toRadians(speedAngle - angle)) * getSpeed();
    }

    /**
     * Gets the lateral speed of the car
     * i.e. the speed with which the car is moving to the direction
     * 90 degrees left to where it's pointing
     * @return The lateral speed of the car
     */
    public double getLateralSpeed() {
        return Math.sin(Math.toRadians(speedAngle - angle)) * getSpeed();
    }

    /**
     * Gets the steering angle of the wheel passed as the parameter
     * @param wheel The wheel we want to find the steering angle of
     * @return The steering angle of the wheel
     */
    public double getSteeringAngle(Wheel wheel) {
        if (wheel == flWheel || wheel == frWheel) {
            return steeringAngle;
        } else {
            return 0;
        }
    }

    /**
     * Returns true if the wheel is one of the front wheels, false otherwise
     * @param wheel The wheel to be checked
     * @return True if the wheel is a front wheel, false otherwise
     */
    public boolean isFrontWheel(Wheel wheel) {
        return (wheel == flWheel || wheel == frWheel);
    }

    /**
     * This method should be called once per com.battlezone.megamachines.physics step
     */
    public void physicsStep() {
//        accelerationAmount = Main.gameInput.isPressed(KeyCode.W) ? 1.0 : 0;
//        brakeAmount = Main.gameInput.isPressed(KeyCode.S) ? 1.0 : 0;
//
//        turnAmount = Main.gameInput.isPressed(KeyCode.A) ? 1.0 : 0;
//        turnAmount = Main.gameInput.isPressed(KeyCode.D) ? (turnAmount - 1.0) : turnAmount;

        steeringAngle = turnAmount * maximumSteeringAngle;

        this.engine.pushTorque(accelerationAmount);

        flWheel.brake(brakeAmount);
        frWheel.brake(brakeAmount);
        blWheel.brake(brakeAmount);
        brWheel.brake(brakeAmount);

        flWheel.computeNewValues();
        frWheel.computeNewValues();
        blWheel.computeNewValues();
        brWheel.computeNewValues();

        this.applyDrag();

        flWheel.physicsStep();
        frWheel.physicsStep();
        blWheel.physicsStep();
        brWheel.physicsStep();

        if (brakeAmount == 0) {
            this.engine.adjustRPM();
        }

        this.addAngle(Math.toDegrees(angularSpeed * PhysicsEngine.getLengthOfTimestamp()));
    }

    public void applyDrag() {
        this.addForce(this.dragCoefficient * Math.pow(this.getSpeed(), 2), -this.getSpeedAngle());
        System.out.println(this.dragCoefficient * Math.pow(this.getSpeed(), 2) + " " + -this.getSpeedAngle());
    }

    public int getModelNumber() {
        return modelNumber;
    }

    public void setTurnAmount(double turnAmount) {
        this.turnAmount = turnAmount;
    }

    public void setAccelerationAmount(double accelerationAmount) {
        this.accelerationAmount = accelerationAmount;
    }

    public void setBrakeAmount(double brakeAmount) {
        this.brakeAmount = brakeAmount;
    }

    @EventListener
    public void setDriverPress(KeyPressEvent event) {
        if (event.getKeyCode() == KeyCode.W) {
            setAccelerationAmount(1.0);
        }
        if (event.getKeyCode() == KeyCode.S) {
            setBrakeAmount(1.0);
        }
        if (event.getKeyCode() == KeyCode.A) {
            setTurnAmount(1.0);
        }
        if (event.getKeyCode() == KeyCode.D) {
            setTurnAmount(-1.0);
        }
    }

    @EventListener
    public void setDriverRelease(KeyReleaseEvent event) {
        if (event.getKeyCode() == KeyCode.W) {
            //TODO: make this a linear movement
            setAccelerationAmount(0.0);
        }
        if (event.getKeyCode() == KeyCode.S) {
            setBrakeAmount(0.0);
        }
        if (event.getKeyCode() == KeyCode.A) {
            if (turnAmount == 1.0) {
                setTurnAmount(0.0);
            }
        }
        if (event.getKeyCode() == KeyCode.D) {
            if (turnAmount == -1.0) {
                setTurnAmount(0.0);
            }
        }
    }

    @Override
    public void draw() {
        getShader().setMatrix4f("rotation", Matrix4f.rotationZ((float) getAngle(), tempMatrix));
        getShader().setVector3f("spriteColour", colour);
        getShader().setMatrix4f("size", Matrix4f.scale(getScale(), tempMatrix));
        getShader().setInt("sampler", 0);
        texture.bind();
        getShader().setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, getXf(), getYf(), 0f, tempMatrix));
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public Shader getShader() {
        return SHADER;
    }

    public Vector3f getColour() {
        return colour;
    }








    //TODO: FILL THOSE OUT
    @Override
    public Pair<Double, Double> getVelocity() {
        return null;
    }

    @Override
    public double getCoefficientOfRestitution() {
        return 0;
    }

    @Override
    public double getMass() {
        return 0;
    }

    @Override
    public double getVectorFromCenterOfMass(double xp, double yp) {
        return 0;
    }

    @Override
    public double getRotationalInertia() {
        return 0;
    }

    @Override
    public double getSpeedVector() {
        return 0;
    }
}
