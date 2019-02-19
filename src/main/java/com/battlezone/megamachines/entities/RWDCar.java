package com.battlezone.megamachines.entities;

import com.battlezone.megamachines.entities.Cars.DordConcentrate;
import com.battlezone.megamachines.entities.abstractCarComponents.*;
import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.physics.Collidable;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.animation.Animatable;
import com.battlezone.megamachines.renderer.game.animation.FallAnimation;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.util.Pair;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * This is a Rear Wheel Drive car
 */
public abstract class RWDCar extends PhysicalEntity implements Drawable, Collidable, Animatable {
    public static final int BYTE_LENGTH = 15;
    private final int indexCount;
    private byte lap;
    private byte position;

    /**
     * The wheelbase of a car is defined as the distance between
     * The front and the back wheels
     */
    protected final double wheelBase;

    /**
     * The amount of weight transfer corrected by the strings
     */
    protected final double springsHardness;

    /**
     * The amount of weight transferred from the front to the back wheels
     */
    protected double longitudinalWeightTransfer;

    /**
     * The height of the center of weight
     */
    protected final double centerOfWeightHeight;

    /**
     * The drag coefficient is used to compute the amount of drag the car experiences when moving
     */
    protected final double dragCoefficient;

    /**
     * The steering angle of this car
     */
    protected double steeringAngle = 0;

    /**
     * The car's maximum steering angle.
     * This is defined as the maximum angle each front wheel can turn
     */
    protected final double maximumSteeringAngle;

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


    public Wheel getFlWheel() {
        return flWheel;
    }

    public Wheel getFrWheel() {
        return frWheel;
    }

    public Wheel getBlWheel() {
        return blWheel;
    }

    public Wheel getBrWheel() {
        return brWheel;
    }

    public double getLongitudinalWeightTransfer() {
        return longitudinalWeightTransfer;
    }

    public void setLongitudinalWeightTransfer(double longitudinalWeightTransfer) {
        this.longitudinalWeightTransfer = longitudinalWeightTransfer;
    }

    private final Model model;

    /**
     * Adds a force vector (over the timie from the last physcs step) to the speed vector
     *
     * @param force The force to be applied
     * @param angle The absolute angle of the force
     */
    public void addForce(Double force, double angle, double l) {
        force *= l;
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

    public RWDCar(double x, double y, float scale, int modelNumber, Vector3f colour, byte lap, byte position, double wheelBase,
                  double maximumSteeringAngle, double dragCoefficient, double centerOfWeightHeight, double springsHardness) {
        super(x, y, scale);
        MessageBus.register(this);
        this.modelNumber = modelNumber;
        this.texture = AssetManager.loadTexture("/cars/car" + modelNumber + ".png");
        this.colour = colour;
        this.model = Model.generateCar();
        this.indexCount = model.getIndices().length;
        this.lap = lap;
        this.position = position;

        this.wheelBase = wheelBase;
        this.maximumSteeringAngle = maximumSteeringAngle;
        this.dragCoefficient = dragCoefficient;
        this.centerOfWeightHeight = centerOfWeightHeight;
        this.springsHardness = springsHardness;
        this.addAnimation(new FallAnimation(this));
    }


    public void setLap(byte lap) {
        this.lap = lap;
    }

    public void setPosition(byte position) {
        this.position = position;
    }

    public byte getLap() {
        return lap;
    }

    public byte getPosition() {
        return position;
    }

    /**
     * TODO: Get weight on a per wheel basis
     */
    public double getLoadOnWheel(Wheel wheel) {
        double weight = this.carBody.getWeight() + this.engine.getWeight() + flWheel.weight + frWheel.weight + blWheel.weight + brWheel.weight;
        if (wheel == flWheel || wheel == frWheel) {
            return (weight * getDistanceToCenterOfWeightLongitudinally(wheel) / wheelBase  - longitudinalWeightTransfer) / 2;
        } else {
            return (weight * getDistanceToCenterOfWeightLongitudinally(wheel) / wheelBase + longitudinalWeightTransfer) / 2;
        }
    }

    //TODO: The center of weight will move in the future

    /**
     * Gets the distance from the center of weight to the rear axle
     *
     * @return The distance from the center of weight to the rear axle
     */
    public double getDistanceCenterOfWeightRearAxle() {
        return wheelBase * (2.0 / 5.0);
    }

    /**
     * Gets the distance from the center of weight to the front axle
     *
     * @return The distance from the center of weight to the front axle
     */
    public double getDistanceCenterOfWeightFrontAxle() {
        return wheelBase - getDistanceCenterOfWeightRearAxle();
    }

    /**
     * Determines on which of the axles the wheel sits and
     * returns the appropiate distance to the center of weight on the longitudinal axis
     *
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

    @Override
    public void correctCollision(Pair<Double, Double> vd, double l) {
        double x = vd.getFirst() * Math.cos(vd.getSecond()) * l;
        double y = vd.getFirst() * Math.sin(vd.getSecond()) * l;

        this.setX(this.getX() - 1.5 * x);
        this.setY(this.getY() - 1.5 * y);

        this.setAngle(this.getAngle() - 2 * this.getAngularSpeed() * l);
    }



    public double getYVelocity() {
        return Math.sin(Math.toRadians(speedAngle)) * getSpeed();
    }

    public double getXVelocity() {
        return Math.cos(Math.toRadians(speedAngle)) * getSpeed();
    }

    /**
     * Gets the longitudinal speed of the car
     * i.e. the speed with which the car is moving to where it's pointing
     *
     * @return The longitudinal speed of the car
     */
    public double getLongitudinalSpeed() {
        return Math.cos(Math.toRadians(speedAngle - angle)) * getSpeed();
    }

    /**
     * Gets the lateral speed of the car
     * i.e. the speed with which the car is moving to the direction
     * 90 degrees left to where it's pointing
     *
     * @return The lateral speed of the car
     */
    public double getLateralSpeed() {
        return Math.sin(Math.toRadians(speedAngle - angle)) * getSpeed();
    }

    /**
     * Gets the steering angle of the wheel passed as the parameter
     *
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
     *
     * @param wheel The wheel to be checked
     * @return True if the wheel is a front wheel, false otherwise
     */
    public boolean isFrontWheel(Wheel wheel) {
        return (wheel == flWheel || wheel == frWheel);
    }

    /**
     * Returns true if the wheel is the front left hweel
     *
     * @param wheel The wheel to be checked
     * @return True if the wheel is a front left wheel, false otherwise
     */
    public boolean isFrontLeftWheel(Wheel wheel) {
        return (wheel == flWheel);
    }

    /**
     * Returns true if the wheel is the front right hweel
     *
     * @param wheel The wheel to be checked
     * @return True if the wheel is a front right wheel, false otherwise
     */
    public boolean isFrontRightWheel(Wheel wheel) {
        return (wheel == frWheel);
    }

    /**
     * Returns true if the wheel is the back left hweel
     *
     * @param wheel The wheel to be checked
     * @return True if the wheel is a back left wheel, false otherwise
     */
    public boolean isBackLeftWheel(Wheel wheel) {
        return (wheel == blWheel);
    }

    /**
     * Returns true if the wheel is the back right hweel
     *
     * @param wheel The wheel to be checked
     * @return True if the wheel is a back right wheel, false otherwise
     */
    public boolean isBackRightWheel(Wheel wheel) {
        return (wheel == brWheel);
    }


    /**
     * This method should be called once per com.battlezone.megamachines.physics step
     */
    public void physicsStep(double l) {
        double oldLongitudinalSpeed = this.getLongitudinalSpeed();
        double oldLateralSpeed = this.getLateralSpeed();

        steeringAngle = turnAmount * maximumSteeringAngle;


        if (brakeAmount > 0 && this.getLongitudinalSpeed() < 2) {
            this.gearbox.engageReverse(true);
        } else if (accelerationAmount > 0) {
            this.gearbox.engageReverse(false);
        }

        if (gearbox.isOnReverse()) {
            this.engine.pushTorque(brakeAmount, l);
        } else {
            this.engine.pushTorque(accelerationAmount, l);
        }

        if (!gearbox.isOnReverse()) {
            flWheel.brake(brakeAmount, l);
            frWheel.brake(brakeAmount, l);
            blWheel.brake(brakeAmount, l);
            brWheel.brake(brakeAmount, l);
        }

        flWheel.computeNewValues(l);
        frWheel.computeNewValues(l);
        blWheel.computeNewValues(l);
        brWheel.computeNewValues(l);

        flWheel.physicsStep(l);
        frWheel.physicsStep(l);
        blWheel.physicsStep(l);
        brWheel.physicsStep(l);

        this.applyDrag(l);

        if (brakeAmount == 0) {
            this.engine.adjustRPM();
        }

        this.addAngle(Math.toDegrees(angularSpeed * l));

        double longitudinalAcceleration = (this.getLongitudinalSpeed() - oldLongitudinalSpeed) / l;
        longitudinalWeightTransfer += (longitudinalAcceleration * this.getMass() * (centerOfWeightHeight / wheelBase)) * l;
        longitudinalWeightTransfer -= l * springsHardness * longitudinalWeightTransfer;
    }

    public void applyDrag(double l) {
        this.addForce(this.dragCoefficient * Math.pow(this.getSpeed(), 2), this.getSpeedAngle() - 180, l);
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
    public void setDriverPressRelease(KeyEvent event) {
        if (event.getPressed())
            setDriverPress(event.getKeyCode());
        else
            setDriverRelease(event.getKeyCode());
    }

    private void setDriverPress(int keyCode) {
        if (keyCode == KeyCode.W) {
            setAccelerationAmount(1.0);
        }
        if (keyCode == KeyCode.S) {
            setBrakeAmount(1.0);
        }
        if (keyCode == KeyCode.A) {
            setTurnAmount(1.0);
        }
        if (keyCode == KeyCode.D) {
            setTurnAmount(-1.0);
        }
    }

    private void setDriverRelease(int keyCode) {
        if (keyCode == KeyCode.W) {
            //TODO: make this a linear movement
            setAccelerationAmount(0.0);
        }
        if (keyCode == KeyCode.S) {
            setBrakeAmount(0.0);
        }
        if (keyCode == KeyCode.A) {
            if (turnAmount == 1.0) {
                setTurnAmount(0.0);
            }
        }
        if (keyCode == KeyCode.D) {
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
        return Shader.CAR;
    }

    public Vector3f getColour() {
        return colour;
    }

    @Override
    public Pair<Double, Double> getVelocity() {
        return new Pair<>(this.getSpeed(), this.getSpeedAngle());
    }

    @Override
    public double getCoefficientOfRestitution() {
        return 0.9;
    }

    @Override
    public double getMass() {
        return this.getWeight();
    }

    @Override
    public double getRotationalInertia() {
        return this.getWeight() * 1;
    }

    @Override
    public Pair<Double, Double> getCenterOfMassPosition() {
        return new Pair<>(this.getX(), this.getY());
    }

    @Override
    public void applyVelocityDelta(Pair<Double, Double> impactResult) {
        double x = getSpeed() * Math.cos(Math.toRadians(speedAngle)) +  impactResult.getFirst() * Math.cos(impactResult.getSecond());
        double y = getSpeed() * Math.sin(Math.toRadians(speedAngle)) + impactResult.getFirst() * Math.sin(impactResult.getSecond());

        setSpeed(Math.sqrt((Math.pow(x, 2) + Math.pow(y, 2))));
        speedAngle = Math.toDegrees(Math.atan2(y, x));
    }

    @Override
    public void applyAngularVelocityDelta(double delta) {
        angularSpeed += delta;
    }

    @Override
    public double getRotation() {
        return this.angle;
    }

    public static byte[] toByteArray(List<RWDCar> cars) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(2+BYTE_LENGTH*cars.size());
        byteBuffer.put((byte)cars.size());
        byteBuffer.put((byte)0); // Player number
        for ( int i = 0; i < cars.size(); i++ )
            byteBuffer.put((byte)(cars.get(i).modelNumber)).put(cars.get(i).getLap()).put(cars.get(i).getPosition()).put(cars.get(i).getColour().toByteArray());
        return byteBuffer.array();
    }

    public static List<RWDCar> fromByteArray(byte[] byteArray, int offset) {
        int len = byteArray[offset];
        ArrayList<RWDCar> cars = new ArrayList<>();
        for ( int i = offset + 2; i < len * BYTE_LENGTH; i+=BYTE_LENGTH ) {
            int modelNumber = byteArray[i];
            byte lap = byteArray[i+1];
            byte position = byteArray[i+2];
            Vector3f colour = Vector3f.fromByteArray(byteArray, i+3);
            DordConcentrate car = new DordConcentrate(0, 0, 1.25f, modelNumber, colour, lap, position);
            cars.add(car);
        }
        return cars;
    }
}
