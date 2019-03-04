package com.battlezone.megamachines.entities;

import com.battlezone.megamachines.ai.Driver;
import com.battlezone.megamachines.entities.Cars.AffordThoroughbred;
import com.battlezone.megamachines.entities.abstractCarComponents.*;
import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.physics.Collidable;
import com.battlezone.megamachines.physics.WorldProperties;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.animation.Animatable;
import com.battlezone.megamachines.renderer.game.animation.Animation;
import com.battlezone.megamachines.renderer.game.animation.FallAnimation;
import com.battlezone.megamachines.renderer.game.animation.LandAnimation;
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
    /**
     * This is used in the networking component of our game
     */
    public static final int BYTE_LENGTH = 15;

    /**
     * This is used for the drawing component of our game
     */
    private final int indexCount;
    /**
     * The car's color
     */
    protected final Vector4f colour;
    /**
     * The lap count for this car
     */
    private byte lap;
    /**
     * This car's position in the race
     */
    private byte position;

    /**
     * True when the car is enlarged by a powerup, false otherwise
     */
    public int isEnlargedByPowerup = 0;

    /**
     * True when the car is affected by an agility powerup
     */
    public int isAgilityActive = 0;

    /**
     * The powerup currently held by this care
     */
    private Powerup currentPowerup;

    /**
     * Gets the current powerup
     * @return The current powerup
     */
    public Powerup getCurrentPowerup() {
        return currentPowerup;
    }

    /**
     * Sets the current powerup of the car
     * @param currentPowerup The current powerup
     */
    public void setCurrentPowerup(Powerup currentPowerup) {
        this.currentPowerup = currentPowerup;
    }

    /**
     * The amount of longitudinal acceleration in the current frame
     */
    double longitudinalAcceleration;

    /**
     * The amount of lateral acceleration in the current frame
     */
    double lateralAcceleration;

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
    protected double longitudinalWeightTransfer = 0;

    /**
     * The amount of weight transferred from the left wheels to the right wheels
     */
    protected double lateralWeightTransfer = 0;

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

    /**
     * A list of animations for this car
     */
    private final List<Animation> animations;

    /**
     * True if the car can be currently controlled, false otherwise
     */
    private boolean controlsActive = true;

    /**
     * The ratio between the distance to the back axle to the center of weight and the front axle to the center of weight
     */
    protected double centerOfWeightRatio;

    /**
     * True if currently playing an animation, false otherwise
     */
    private byte currentlyPlayingAnimation;

    /**
     * Gets the longitudinal weight transfer
     * @return The longitudinal weight transfer
     */
    public double getLongitudinalWeightTransfer() {
        if (isAgilityActive > 0) {
            return 0;
        } else {
            return longitudinalWeightTransfer;
        }
    }

    /**
     * Gets the lateral weight transfer
     * @return The lateral weight transfer
     */
    public double getLateralWeightTransfer() {
        if (isAgilityActive > 0) {
            return 0;
        } else {
            return lateralWeightTransfer;
        }
    }


    /**
     * Sets the longitudinal weight transfer
     * @param longitudinalWeightTransfer The longitudinal weight transfer
     */
    public void setLongitudinalWeightTransfer(double longitudinalWeightTransfer) {
        this.longitudinalWeightTransfer = longitudinalWeightTransfer;
    }

    /**
     * The car's model
     */
    private final Model model;

    /**
     * The car's driver
     */
    private Driver driver;

    private DeathCloud cloud;

    /**
     * Adds a force vector (over the timie from the last physics step) to the speed vector
     *
     * @param force The force to be applied
     * @param angle The absolute angle of the force
     */
    public void addForce(Double force, double angle, double l) {
        force *= l;
        force /= this.getWeight();

        longitudinalAcceleration += force * Math.cos(Math.toRadians(angle - this.getAngle()));
        lateralAcceleration += force * Math.sin(Math.toRadians(angle - this.getAngle()));

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
        return this.carBody.getWeight() + this.engine.getWeight() + flWheel.weight + frWheel.weight + blWheel.weight + brWheel.weight;
    }

    /**
     * Returns the car's body
     * @return The car's body
     */
    public CarBody getCarBody() {
        return this.carBody;
    }

    /**
     * Returns this car's back differential
     * @return This car's back differential
     */
    public Differential getBackDifferential() {
        return this.backDifferential;
    }

    /**
     * Returns this car's drive shaft
     * @return This car's drive shaft
     */
    public DriveShaft getDriveShaft() {
        return this.driveShaft;
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

    /**
     * Gets the front left wheel of the car
     * @return The front left wheel of the car
     */
    public Wheel getFlWheel() {
        return flWheel;
    }

    /**
     * Gets the front right wheel of the car
     * @return The front right wheel of the car
     */
    public Wheel getFrWheel() {
        return frWheel;
    }

    /**
     * Gets the back left wheel of the car
     * @return The back left wheel of the car
     */
    public Wheel getBlWheel() {
        return blWheel;
    }

    /**
     * Gets the back right wheel of the car
     * @return The back right wheel of the car
     */
    public Wheel getBrWheel() {
        return brWheel;
    }

    /**
     * The constructor
     * @param x The x position of the car
     * @param y The y position of the car
     * @param scale The scale of the car
     * @param modelNumber The model number of the car
     * @param colour The colour of the car
     * @param lap The car's current lap count
     * @param position The position of the car
     * @param wheelBase The wheel base of the car
     * @param maximumSteeringAngle The maximum steering angle of the car
     * @param dragCoefficient The drag coefficient of the car
     * @param centerOfWeightHeight The height of this car's center of weight
     * @param springsHardness The spring hardness of this car
     */
    public RWDCar(double x, double y, float scale, int modelNumber, Vector3f colour, byte lap, byte position, double wheelBase,
                  double maximumSteeringAngle, double dragCoefficient, double centerOfWeightHeight, double springsHardness, double centerOfWeightRatio) {
        super(x, y, scale);
        MessageBus.register(this);
        this.colour = new Vector4f(colour, 1f);
        this.modelNumber = modelNumber;
        this.texture = AssetManager.loadTexture("/cars/car" + modelNumber + ".png");
        this.model = Model.generateCar();
        this.indexCount = model.getIndices().length;
        this.lap = lap;
        this.position = position;
        this.centerOfWeightRatio = centerOfWeightRatio;

        this.wheelBase = wheelBase;
        this.maximumSteeringAngle = maximumSteeringAngle;
        this.dragCoefficient = dragCoefficient;
        this.centerOfWeightHeight = centerOfWeightHeight;
        this.springsHardness = springsHardness;
        this.animations = new ArrayList<>();
        this.animations.add(new FallAnimation(this));
        this.animations.add(new LandAnimation(this));
    }

    /**
     * Sets the lap of this car
     * @param lap The lap of this car
     */
    public void setLap(byte lap) {
        this.lap = lap;
    }

    /**
     * Sets the position of this car
     * @param position The position of this car
     */
    public void setPosition(byte position) {
        this.position = position;
    }

    /**
     * Gets the lap the car is currently in
     * @return The lap the car is currently in
     */
    public byte getLap() {
        return lap;
    }

    /**
     * Gets the position of this car
     * @return The position of this car
     */
    public byte getPosition() {
        return position;
    }

    /**
     * Gets the load on wheel
     * @param wheel The wheel
     * @return The load
     */
    public double getLoadOnWheel(Wheel wheel) {
        double weight = this.getWeight();

        if (isAgilityActive > 0) {
            weight *= 2;
        }

        if (wheel == flWheel || wheel == frWheel) {
            double weightOnAxle = (weight * getDistanceToCenterOfWeightLongitudinally(wheel) / wheelBase  - getLongitudinalWeightTransfer()) / 2;
            if (wheel == flWheel) {
                return weightOnAxle - (getLateralWeightTransfer() / 2);
            } else {
                return weightOnAxle + (getLateralWeightTransfer() / 2);
            }
        } else {
            double weightOnAxle = (weight * getDistanceToCenterOfWeightLongitudinally(wheel) / wheelBase + getLongitudinalWeightTransfer()) / 2;
            if (wheel == blWheel) {
                return weightOnAxle - (getLateralWeightTransfer() / 2);
            } else {
                return weightOnAxle + (getLateralWeightTransfer() / 2);
            }
        }
    }

    /**
     * Gets the distance from the center of weight to the rear axle
     *
     * @return The distance from the center of weight to the rear axle
     */
    public double getDistanceCenterOfWeightRearAxle() {
        return wheelBase * centerOfWeightRatio;
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

    /**
     * Gets the car's velocity on the Y axis
     * @return The car's velocity on the Y axis
     */
    public double getYVelocity() {
        return Math.sin(Math.toRadians(speedAngle)) * getSpeed();
    }

    @Override
    public boolean isEnlargedByPowerup() {
        return isEnlargedByPowerup > 0;
    }

    /**
     * Gets the car's velocity on the X axis
     * @return The car's velocity on the X axis
     */
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

        longitudinalAcceleration = 0;
        lateralAcceleration = 0;

        steeringAngle = turnAmount * maximumSteeringAngle;

        if (!controlsActive) {
            accelerationAmount = 0;
            brakeAmount = 0;
            steeringAngle = 0;
        }

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
            this.engine.setRPM(gearbox.getNewRPM());
        }

        this.addAngle(Math.toDegrees(angularSpeed * l));

        longitudinalWeightTransfer += (longitudinalAcceleration * this.getMass() * (centerOfWeightHeight / wheelBase));
        longitudinalWeightTransfer -= l * springsHardness * longitudinalWeightTransfer;

        lateralWeightTransfer += (lateralAcceleration / WorldProperties.g) * this.getMass() * (centerOfWeightHeight / this.getPhysicsWidth());
        lateralWeightTransfer -= l * springsHardness * lateralWeightTransfer;
    }

    /**
     * Applies drag to this car
     * @param l The length of the last physics timestamp
     */
    public void applyDrag(double l) {
        if (isAgilityActive == 0) {
            this.addForce(this.dragCoefficient * Math.pow(this.getSpeed(), 2), this.getSpeedAngle() - 180, l);
        }
    }

    /**
     * Gets the model number of the car
     * @return The model number of the car
     */
    public int getModelNumber() {
        return modelNumber;
    }

    /**
     * Sets the turn amount of this car
     * @param turnAmount The turn amount of this car
     */
    public void setTurnAmount(double turnAmount) {
        this.turnAmount = turnAmount;
    }

    /**
     * Sets the acceleration amount of this car
     * @param accelerationAmount The acceleration amount of this car
     */
    public void setAccelerationAmount(double accelerationAmount) {
        this.accelerationAmount = accelerationAmount;
    }

    /**
     * Sets the brake amount of this car
     * @param brakeAmount The brake amount of this car
     */
    public void setBrakeAmount(double brakeAmount) {
        this.brakeAmount = brakeAmount;
    }

    @EventListener
    public void setDriverPressRelease(KeyEvent event) {
        if (driver == null) {
            if (event.getPressed())
                setDriverPress(event.getKeyCode());
            else
                setDriverRelease(event.getKeyCode());
        }
    }

    /**
     * Reacts to a key being pressed
     * @param keyCode The code of the key being pressed
     */
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
        if (keyCode == KeyCode.SPACE) {
            if (currentPowerup != null) {
                currentPowerup.activate();
            }
        }
    }

    /**
     * Reacts to a key getting released
     * @param keyCode The key code of the key getting released
     */
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
        getShader().setVector4f("spriteColour", getColour());
        getShader().setMatrix4f("size", Matrix4f.scale(getScale(), tempMatrix));
        getShader().setInt("sampler", 0);
        texture.bind();
        getShader().setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, getXf(), getYf(), 0f, tempMatrix));
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
        cloud.draw();
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public Shader getShader() {
        return Shader.CAR;
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
        if (isAgilityActive > 0) {
            return new Pair<>(this.getX(), this.getY());
        }



        //We're making these smaller purposefully
        Pair<Double, Double> halfOfLengthv = new Pair<>(this.getLength() / 2, this.getAngle());
        Pair<Double, Double> halfOfWidthv = new Pair<>(this.getWidth() / 2, this.getAngle() + 90);

        Pair<Double, Double> halfOfLength =
                new Pair<>(halfOfLengthv.getFirst() * Math.cos(Math.toRadians(halfOfLengthv.getSecond())),
                        halfOfLengthv.getFirst() * Math.sin(Math.toRadians(halfOfLengthv.getSecond())));

        Pair<Double, Double> halfOfWidth =
                new Pair<>(halfOfWidthv.getFirst() * Math.cos(Math.toRadians(halfOfWidthv.getSecond())),
                        halfOfWidthv.getFirst() * Math.sin(Math.toRadians(halfOfWidthv.getSecond())));

        Pair<Double, Double> fl = new Pair<>(this.getX() + halfOfLength.getFirst() + halfOfWidth.getFirst(), this.getY() + halfOfLength.getSecond() + halfOfWidth.getSecond());
        Pair<Double, Double> fr = new Pair<>(this.getX() + halfOfLength.getFirst() - halfOfWidth.getFirst(), this.getY() + halfOfLength.getSecond() - halfOfWidth.getSecond());
        Pair<Double, Double> bl = new Pair<>(this.getX() - halfOfLength.getFirst() + halfOfWidth.getFirst(), this.getY() - halfOfLength.getSecond() + halfOfWidth.getSecond());
        Pair<Double, Double> br = new Pair<>(this.getX() - halfOfLength.getFirst() - halfOfWidth.getFirst(), this.getY() - halfOfLength.getSecond() - halfOfWidth.getSecond());

        return new Pair<>((fl.getFirst() * this.getLoadOnWheel(flWheel) +
                fr.getFirst() * this.getLoadOnWheel(frWheel) +
                bl.getFirst() * this.getLoadOnWheel(blWheel) +
                br.getFirst() * this.getLoadOnWheel(brWheel)) / this.getMass(),
                (fl.getSecond() * this.getLoadOnWheel(flWheel) +
                        fr.getSecond() * this.getLoadOnWheel(frWheel) +
                        bl.getSecond() * this.getLoadOnWheel(blWheel) +
                        br.getSecond() * this.getLoadOnWheel(brWheel)) / this.getMass());
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

    /**
     * Creates cars from a byte array
     * @param byteArray The byte array
     * @param offset The offset
     * @return The cars
     */
    public static List<RWDCar> fromByteArray(byte[] byteArray, int offset) {
        int len = byteArray[offset];
        ArrayList<RWDCar> cars = new ArrayList<>();
        for ( int i = offset + 2; i < len * BYTE_LENGTH; i+=BYTE_LENGTH ) {
            int modelNumber = byteArray[i];
            byte lap = byteArray[i+1];
            byte position = byteArray[i+2];
            Vector3f colour = Vector3f.fromByteArray(byteArray, i+3);
            AffordThoroughbred car = new AffordThoroughbred(0, 0, 1.25f, modelNumber, colour, lap, position);
            cars.add(car);
        }
        return cars;
    }

    @Override
    public List<Animation> getAnimations() {
        return animations;
    }

    /**
     * Sets controlsActive
     * @param controlsActive True if the controls should be active, false otherwise
     */
    public void setControlsActive(boolean controlsActive) {
        this.controlsActive = controlsActive;
    }

    /**
     * Returns true if the controls are active, false otherwise
     * @return true if the controls are active, fales otherwise
     */
    public boolean isControlsActive() {
        return controlsActive;
    }

    @Override
    public void setCurrentlyPlaying(int currentlyPlaying) {
        this.currentlyPlayingAnimation = (byte) currentlyPlaying;
    }

    @Override
    public int getCurrentlyPlaying() {
        return currentlyPlayingAnimation;
    }

    /**
     * Sets the driver of this car
     * @param driver The driver of this car
     */
    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    /**
     * Gets the driver of this car
     * @return The driver of this car
     */
    public Driver getDriver() {
        return driver;
    }

    @Override
    public double getLength() {
        return getScale();
    }

    @Override
    public double getWidth() {
        return getScale() / 2;
    }

    /**
     * Gets the colour of this car
     * @return the colour of this car
     */
    public Vector4f getColour() {
        return colour;
    }

    /**
     * This function gets called when an agility powerup has been activated for this car
     */
    public void agilityActivated() {
        isAgilityActive++;
    }

    /**
     * This function gets called when an agility powerup has been deactivated for this car
     */
    public void agilityDeactivated() {
        isAgilityActive--;
    }

    /**
     * This function gets called an a growth powerup has been activated for this car
     */
    public void growthActivated() {
        this.isEnlargedByPowerup++;
    }

    /**
     * This function gets called when a growth powerup has been deactivated for this car
     */
    public void growthDeactivated() {
        this.isEnlargedByPowerup--;
    }

    public void setCloud(DeathCloud cloud) {
        this.cloud = cloud;
    }

    public void playCloud() {
        if (cloud != null) {
            cloud.play(getX(), getY());
        }
    }
}
