package com.battlezone.megamachines.entities;

import com.battlezone.megamachines.ai.Driver;
import com.battlezone.megamachines.entities.cars.AffordThoroughbred;
import com.battlezone.megamachines.entities.cars.components.abstracted.*;
import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector2d;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.physics.Collidable;
import com.battlezone.megamachines.physics.WorldProperties;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.Renderer;
import com.battlezone.megamachines.renderer.game.animation.Animatable;
import com.battlezone.megamachines.renderer.game.animation.Animation;
import com.battlezone.megamachines.renderer.game.animation.FallAnimation;
import com.battlezone.megamachines.renderer.game.animation.LandAnimation;
import com.battlezone.megamachines.renderer.game.particle.AgilityParticleEffect;
import com.battlezone.megamachines.renderer.game.particle.DriftParticleEffect;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.util.Pair;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * This is a Rear Wheel Drive car
 */
public abstract class RWDCar extends PhysicalEntity implements Drawable, Collidable, Animatable, Controllable, PowerupUser, WheeledObject, Car {
    /**
     * This is used in the networking component of our game
     */
    public static final int BYTE_LENGTH = 35;
    /**
     * The wheelbase of a car is defined as the distance between
     * The front and the back wheels
     */
    public final double wheelBase;
    /**
     * The car's color
     */
    protected final Vector4f colour;
    /**
     * The amount of weight transfer corrected by the strings
     */
    protected final double springsHardness;
    /**
     * The height of the center of weight
     */
    protected final double centerOfWeightHeight;
    /**
     * The drag coefficient is used to compute the amount of drag the car experiences when moving
     */
    protected final double dragCoefficient;
    /**
     * The car's maximum steering angle.
     * This is defined as the maximum angle each front wheel can turn
     */
    protected final double maximumSteeringAngle;
    /**
     * This is used for the drawing component of our game
     */
    private final int indexCount;
    /**
     * This car's model number
     */
    private final int modelNumber;
    /**
     * The car's texture
     */
    private final Texture texture;
    /**
     * A list of animations for this car
     */
    private final List<Animation> animations;
    /**
     * The car's model
     */
    private final Model model;
    /**
     * True when the car is enlarged by a powerup, false otherwise
     */
    public int isEnlargedByPowerup = 0;
    /**
     * True when the car is affected by an agility powerup
     */
    public int isAgilityActive = 0;
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
     * The amount of weight transferred from the front to the back wheels
     */
    protected double longitudinalWeightTransfer = 0;
    /**
     * The amount of weight transferred from the left wheels to the right wheels
     */
    protected double lateralWeightTransfer = 0;
    /**
     * The steering angle of this car
     */
    protected double steeringAngle = 0;
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
     * The ratio between the distance to the back axle to the center of weight and the front axle to the center of weight
     */
    protected double centerOfWeightRatio;
    /**
     * The amount of longitudinal acceleration in the current frame
     */
    double longitudinalAcceleration;
    /**
     * The amount of lateral acceleration in the current frame
     */
    double lateralAcceleration;
    /**
     * The lap count for this car
     */
    private byte lap;
    /**
     * This car's position in the race
     */
    private byte position;
    private int depth = 0;
    /**
     * The powerup currently held by this car
     */
    private Powerup currentPowerup;
    /**
     * A temporary matrix used for computing the corners of the car
     */
    private Matrix4f tempMatrix = new Matrix4f();
    /**
     * True if the car can be currently controlled, false otherwise
     */
    private boolean controlsActive = true;

    /**
     * True if the game is paused
     */
    private boolean isPaused = false;
    /**
     * True if currently playing an animation, false otherwise
     */
    private byte currentlyPlayingAnimation;
    /**
     * The car's driver
     */
    private Driver driver;

    /**
     * The car'd death cloud
     */
    private DeathCloud cloud;

    private AgilityParticleEffect agilityParticleEffect;

    private DriftParticleEffect dustParticleEffect;

    private String name;

    /**
     * The constructor
     *
     * @param x                    The x position of the car
     * @param y                    The y position of the car
     * @param scale                The scale of the car
     * @param modelNumber          The model number of the car
     * @param colour               The colour of the car
     * @param lap                  The car's current lap count
     * @param position             The position of the car
     * @param wheelBase            The wheel base of the car
     * @param maximumSteeringAngle The maximum steering angle of the car
     * @param dragCoefficient      The drag coefficient of the car
     * @param centerOfWeightHeight The height of this car's center of weight
     * @param springsHardness      The spring hardness of this car
     */
    public RWDCar(double x, double y, float scale, int modelNumber, Vector3f colour, byte lap, byte position, double wheelBase,
                  double maximumSteeringAngle, double dragCoefficient, double centerOfWeightHeight, double springsHardness, double centerOfWeightRatio, String name) {
        super(x, y, scale);
        MessageBus.register(this);
        this.colour = new Vector4f(colour, 1f);
        this.modelNumber = modelNumber;
        this.texture = AssetManager.loadTexture("/cars/car" + modelNumber + ".png");
        this.model = Model.CAR;
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

        this.name = name;
    }

    /**
     * Stores cars to a byte array
     *
     * @param cars The cars
     * @return The byte array
     */
    public static byte[] toByteArray(List<RWDCar> cars) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(2 + BYTE_LENGTH * cars.size());
        byteBuffer.put((byte) cars.size());
        byteBuffer.put((byte) 0); // Player number
        for (int i = 0; i < cars.size(); i++) {
            var name = new byte[20];
            System.arraycopy(cars.get(i).getName().getBytes(), 0, name, 0, cars.get(i).getName().getBytes().length);
            byteBuffer.put((byte) (cars.get(i).modelNumber)).put(cars.get(i).getLap()).put(cars.get(i).getPosition()).put(cars.get(i).getColour().toByteArray()).put(name);
        }
        return byteBuffer.array();
    }

    /**
     * Creates cars from a byte array
     *
     * @param byteArray The byte array
     * @param offset    The offset
     * @return The cars
     */
    public static List<RWDCar> fromByteArray(byte[] byteArray, int offset) {
        int len = byteArray[offset];
        ArrayList<RWDCar> cars = new ArrayList<>();
        for (int i = offset + 2; i < len * BYTE_LENGTH; i += BYTE_LENGTH) {
            int modelNumber = byteArray[i];
            byte lap = byteArray[i + 1];
            byte position = byteArray[i + 2];
            Vector3f colour = Vector3f.fromByteArray(byteArray, i + 3);
            byte[] name = new byte[20];
            System.arraycopy(byteArray, i + 15, name, 0, 20);
            AffordThoroughbred car = new AffordThoroughbred(0, 0, 1.25f, modelNumber, colour, lap, position, new String(name).trim());
            cars.add(car);
        }
        return cars;
    }

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

    @Override
    public double getLongitudinalWeightTransfer() {
        if (isAgilityActive > 0) {
            return 0;
        } else {
            return longitudinalWeightTransfer;
        }
    }

    /**
     * Sets the longitudinal weight transfer
     *
     * @param longitudinalWeightTransfer The longitudinal weight transfer
     */
    public void setLongitudinalWeightTransfer(double longitudinalWeightTransfer) {
        this.longitudinalWeightTransfer = longitudinalWeightTransfer;
    }

    @Override
    public double getLateralWeightTransfer() {
        if (isAgilityActive > 0) {
            return 0;
        } else {
            return lateralWeightTransfer;
        }
    }

    @Override
    public CarBody getCarBody() {
        return this.carBody;
    }

    @Override
    public Differential getBackDifferential() {
        return this.backDifferential;
    }

    @Override
    public DriveShaft getDriveShaft() {
        return this.driveShaft;
    }

    @Override
    public Engine getEngine() {
        return this.engine;
    }

    @Override
    public Gearbox getGearbox() {
        return this.gearbox;
    }

    @Override
    public Wheel getFlWheel() {
        return flWheel;
    }

    @Override
    public Wheel getFrWheel() {
        return frWheel;
    }

    @Override
    public Wheel getBlWheel() {
        return blWheel;
    }

    @Override
    public Wheel getBrWheel() {
        return brWheel;
    }

    @Override
    public Powerup getCurrentPowerup() {
        if (currentlyPlayingAnimation == 0) {
            return currentPowerup;
        } else {
            return null;
        }
    }

    @Override
    public void setCurrentPowerup(Powerup currentPowerup) {
        this.currentPowerup = currentPowerup;
    }

    /**
     * Gets the lap the car is currently in
     *
     * @return The lap the car is currently in
     */
    public byte getLap() {
        return lap;
    }

    /**
     * Sets the lap of this car
     *
     * @param lap The lap of this car
     */
    public void setLap(byte lap) {
        this.lap = lap;
    }

    /**
     * Gets the position of this car
     *
     * @return The position of this car
     */
    public byte getPosition() {
        return position;
    }

    /**
     * Sets the position of this car
     *
     * @param position The position of this car
     */
    public void setPosition(byte position) {
        this.position = position;
    }

    @Override
    public double getDistanceCenterOfWeightRearAxle() {
        return wheelBase * centerOfWeightRatio;
    }

    @Override
    public double getDistanceCenterOfWeightFrontAxle() {
        return wheelBase - getDistanceCenterOfWeightRearAxle();
    }

    @Override
    public void correctCollision(Vector2d vd, double l) {
        double x = vd.x * Math.cos(vd.y) * l;
        double y = vd.x * Math.sin(vd.y) * l;

        this.setX(this.getX() - 1.5 * x);
        this.setY(this.getY() - 1.5 * y);

        this.setAngle(this.getAngle() - 2 * this.getAngularSpeed() * l);
    }

    /**
     * Gets the car's velocity on the Y axis
     *
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
     *
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

    @Override
    public boolean isFrontWheel(Wheel wheel) {
        return (wheel == flWheel || wheel == frWheel);
    }

    @Override
    public boolean isFrontLeftWheel(Wheel wheel) {
        return (wheel == flWheel);
    }

    @Override
    public boolean isFrontRightWheel(Wheel wheel) {
        return (wheel == frWheel);
    }

    @Override
    public boolean isBackLeftWheel(Wheel wheel) {
        return (wheel == blWheel);
    }

    @Override
    public boolean isBackRightWheel(Wheel wheel) {
        return (wheel == brWheel);
    }

    /**
     * This method should be called once per com.battlezone.megamachines.physics step
     */
    public void physicsStep(double l, WorldProperties worldProperties) {
        double oldLongitudinalSpeed = this.getLongitudinalSpeed();
        double oldLateralSpeed = this.getLateralSpeed();

        longitudinalAcceleration = 0;
        lateralAcceleration = 0;

        steeringAngle = turnAmount * maximumSteeringAngle;

        if (!controlsActive || isPaused) {
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

        flWheel.computeNewValues(l, worldProperties);
        frWheel.computeNewValues(l, worldProperties);
        blWheel.computeNewValues(l, worldProperties);
        brWheel.computeNewValues(l, worldProperties);

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

        lateralWeightTransfer += (lateralAcceleration / worldProperties.g) * this.getMass() * (centerOfWeightHeight / this.getPhysicsWidth());
        lateralWeightTransfer -= l * springsHardness * lateralWeightTransfer;
    }

    /**
     * Applies drag to this car
     *
     * @param l The length of the last physics timestamp
     */
    public void applyDrag(double l) {
        if (isAgilityActive == 0) {
            this.addForce(this.dragCoefficient * Math.pow(this.getSpeed(), 2), this.getSpeedAngle() - 180, l);
        }
    }

    /**
     * Gets the model number of the car
     *
     * @return The model number of the car
     */
    public int getModelNumber() {
        return modelNumber;
    }

    @Override
    public double getTurnAmount() {
        return turnAmount;
    }

    @Override
    public void setTurnAmount(double turnAmount) {
        this.turnAmount = turnAmount;
    }

    @Override
    public double getAccelerationAmount() {
        return accelerationAmount;
    }

    @Override
    public void setAccelerationAmount(double accelerationAmount) {
        this.accelerationAmount = accelerationAmount;
    }

    @Override
    public double getBrakeAmount() {
        return brakeAmount;
    }

    @Override
    public void setBrakeAmount(double brakeAmount) {
        this.brakeAmount = brakeAmount;
    }

    @Override
    public void draw() {
        texture.bind();
        getShader().setMatrix4f("rotation", Matrix4f.rotationZ((float) getAngle(), tempMatrix));
        getShader().setVector4f("spriteColour", getColour());
        getShader().setMatrix4f("size", Matrix4f.scale(getScale(), tempMatrix));
        getShader().setInt("sampler", 0);
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

    @Override
    public Vector2d getVelocity() {
        return new Vector2d(this.getSpeed(), this.getSpeedAngle());
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
    public Vector2d getCenterOfMassPosition() {
        if (isAgilityActive > 0) {
            return new Vector2d(this.getX(), this.getY());
        }

        // We're making these smaller purposefully
        final double halfOfLengthvX = this.getLength() / 2, halfOfLengthvY = this.getAngle(),
                halfOfWidthvX = this.getWidth() / 2, halfOfWidthvY = this.getAngle() + 90,
                halfOfLengthX = halfOfLengthvX * Math.cos(Math.toRadians(halfOfLengthvY)), halfOfLengthY = halfOfLengthvX * Math.sin(Math.toRadians(halfOfLengthvY)),
                halfOfWidthX = halfOfWidthvX * Math.cos(Math.toRadians(halfOfWidthvY)), halfOfWidthY = halfOfWidthvX * Math.sin(Math.toRadians(halfOfWidthvY)),
                flX = this.getX() + halfOfLengthX + halfOfWidthX, flY = this.getY() + halfOfLengthY + halfOfWidthY,
                frX = this.getX() + halfOfLengthX - halfOfWidthX, frY = this.getY() + halfOfLengthY - halfOfWidthY,
                blX = this.getX() - halfOfLengthX + halfOfWidthX, blY = this.getY() - halfOfLengthY + halfOfWidthY,
                brX = this.getX() - halfOfLengthX - halfOfWidthX, brY = this.getY() - halfOfLengthY - halfOfWidthY;

        return new Vector2d((flX * this.getLoadOnWheel(flWheel, this.getWeight(), isAgilityActive, this.wheelBase) +
                frX * this.getLoadOnWheel(frWheel, this.getWeight(), isAgilityActive, this.wheelBase) +
                blX * this.getLoadOnWheel(blWheel, this.getWeight(), isAgilityActive, this.wheelBase) +
                brX * this.getLoadOnWheel(brWheel, this.getWeight(), isAgilityActive, this.wheelBase)) / this.getMass(),
                (flY * this.getLoadOnWheel(flWheel, this.getWeight(), isAgilityActive, this.wheelBase) +
                        frY * this.getLoadOnWheel(frWheel, this.getWeight(), isAgilityActive, this.wheelBase) +
                        blY * this.getLoadOnWheel(blWheel, this.getWeight(), isAgilityActive, this.wheelBase) +
                        brY * this.getLoadOnWheel(brWheel, this.getWeight(), isAgilityActive, this.wheelBase)) / this.getMass());
    }

    @Override
    public void applyVelocityDelta(Pair<Double, Double> impactResult) {
        double x = getSpeed() * Math.cos(Math.toRadians(speedAngle)) + impactResult.getFirst() * Math.cos(impactResult.getSecond());
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

    @Override
    public List<Animation> getAnimations() {
        return animations;
    }

    @Override
    public boolean isControlsActive() {
        return controlsActive;
    }

    @Override
    public void setControlsActive(boolean controlsActive) {
        this.controlsActive = controlsActive;
    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    @Override
    public int getCurrentlyPlaying() {
        return currentlyPlayingAnimation;
    }

    @Override
    public void setCurrentlyPlaying(int currentlyPlaying) {
        this.currentlyPlayingAnimation = (byte) currentlyPlaying;
    }

    @Override
    public Driver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(Driver driver) {
        this.driver = driver;
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
     *
     * @return the colour of this car
     */
    public Vector4f getColour() {
        return colour;
    }

    @Override
    public void agilityActivated() {
        isAgilityActive++;
    }

    @Override
    public void agilityDeactivated() {
        isAgilityActive--;
    }

    @Override
    public void growthActivated() {
        this.isEnlargedByPowerup++;
    }

    @Override
    public void growthDeactivated() {
        this.isEnlargedByPowerup--;
    }

    public void setCloud(DeathCloud cloud) {
        this.cloud = cloud;
    }

    public void setDustParticles(DriftParticleEffect e) {
        dustParticleEffect = e;
    }

    public void setAgilityParticles(AgilityParticleEffect e) {
        agilityParticleEffect = e;
    }

    public void playCloud() {
        if (cloud != null) {
            cloud.play(getX(), getY(), this.isEnlargedByPowerup);
        }
    }

    @Override
    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        if (this.depth == depth)
            return;
        this.depth = depth;
        Renderer.getInstance().populateRenderables();
    }

    public String getName() {
        return name;
    }
}
