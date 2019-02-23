package com.battlezone.megamachines.entities;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.physics.Collidable;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.GameObject;

import java.util.List;

/**
 * All the physical game com.battlezone.megamachines.entities should extend this class.
 * Here, we hold the basic properties of all phyisical com.battlezone.megamachines.entities.
 */
public abstract class PhysicalEntity extends GameObject implements Collidable {

    /**
     * The speed at which this object is moving in meters per second
     */
    private double speed;

    /**
     * The angular speed of the car. Positive to the left, negative to the right.
     */
    public double angularSpeed;

    private final Matrix4f rotation = new Matrix4f();

    private final Vector4f frontLeft = new Vector4f(0, 0, 0, 0);
    private final Vector4f frontRight = new Vector4f(0, 0, 0, 0);
    private final Vector4f backRight = new Vector4f(0, 0, 0, 0);
    private final Vector4f backLeft = new Vector4f(0, 0, 0, 0);

    private final List<Pair<Double, Double>> corners = List.of(
            new Pair<>(0.0, 0.0),
            new Pair<>(0.0, 0.0),
            new Pair<>(0.0, 0.0),
            new Pair<>(0.0, 0.0)
    );

    private final List<List<Pair<Double, Double>>> hitboxes = List.of(corners);

    private Matrix4f tempMatrix = new Matrix4f();
    /**
     * The angle is the angle at which the car is pointing
     * We are using the trigonometric interpretation of angles (with degrees, not radians)
     * An angle of 0 degrees means that the entity is pointing to the right.
     * An angle of 90 degrees means that the entity is pointing upwards.
     * An angle of -90 degrees means that the entity is pointing downwards.
     */
    protected double angle = 90.0;

    /**
     * The speed angle is the angle at which the car is moving
     */
    protected double speedAngle = 90.0;

    public PhysicalEntity(double x, double y, float scale) {
        super(x, y, scale);
    }

    /**
     * Gets the Physical Entity's length
     *
     * @return The length
     */
    public abstract double getLength();

    /**
     * Gets the Phyisical Entity's width
     *
     * @return The width
     */
    public abstract double getWidth();

    /**
     * Gets the Physical Entity's length for the physics part
     *
     * @return The length
     */
    public double getPhysicsLength() {
        return 3 * getLength();
    }

    /**
     * Gets the Phyisical Entity's width for the physics part
     *
     * @return The width
     */
    public double getPhysicsWidth() {
        return 3 * getWidth();
    }


    /**
     * Gets the Physical Entity's angle
     *
     * @return The angle
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Gets the angle of the speed vector
     *
     * @return The angle of the speed vector
     */
    public double getSpeedAngle() {
        return speedAngle;
    }

    public void setSpeedAngle(double speedAngle) {
        this.speedAngle = speedAngle;
    }

    /**
     * Sets the Physical Entity's angle
     *
     * @param angle The angle to be set
     */
    public void setAngle(double angle) {
        this.angle = angle;
    }

    /**
     * Adds an angle to the Physical Entity's angle
     *
     * @param angle The angle to be added
     */
    public void addAngle(double angle) {
        this.angle += angle;
    }

    @Override
    public List<List<Pair<Double, Double>>> getCornersOfAllHitBoxes() {

        Matrix4f.rotationZ((float) -angle, rotation);

        // Actual screen sizes are slightly smaller
        double length = getLength() - 0.1f;
        double width = getWidth() - 0.15f;

        frontLeft.x = (float) length;
        frontLeft.y = (float) width;
        rotation.multiply(frontLeft, frontLeft);
        corners.get(0).setFirst((double) frontLeft.x + getX());
        corners.get(0).setSecond((double) frontLeft.y + getY());

        frontRight.x = (float) length;
        frontRight.y = (float) -width;
        rotation.multiply(frontRight, frontRight);
        corners.get(1).setFirst((double) frontRight.x + getX());
        corners.get(1).setSecond((double) frontRight.y + getY());

        backRight.x = (float) -length;
        backRight.y = (float) -width;
        rotation.multiply(backRight, backRight);
        corners.get(2).setFirst((double) backRight.x + getX());
        corners.get(2).setSecond((double) backRight.y + getY());

        backLeft.x = (float) -length;
        backLeft.y = (float) width;
        rotation.multiply(backLeft, backLeft);
        corners.get(3).setFirst((double) backLeft.x + getX());
        corners.get(3).setSecond((double) backLeft.y + getY());

//        System.out.println(corners);

        return hitboxes;
    }

    /**
     * Gets this object's speed
     *
     * @return The object's speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the object's speed
     *
     * @param speed The speed
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Sets the object's angular speed
     *
     * @param speed The speed
     */
    public void setAngularSpeed(double speed) {
        this.angularSpeed = speed;
    }

    /**
     * Gets the object's angular speed
     *
     * @return The object's angular speed
     */
    public double getAngularSpeed() {
        return this.angularSpeed;
    }

}
