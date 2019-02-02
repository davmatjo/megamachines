package com.battlezone.megamachines.entities;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.physics.Collidable;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.GameObject;

import java.util.ArrayList;
import java.util.List;

/**
 * All the physical game com.battlezone.megamachines.entities should extend this class.
 * Here, we hold the basic properties of all phyisical com.battlezone.megamachines.entities.
 */
public abstract class PhysicalEntity extends GameObject implements Collidable {
    /**
     * The entity's length in meters
     */
    private double length = getScale();

    /**
     * The entity's width in meters
     */
    private double width = getScale() / 2;

    /**
     * The speed at which this object is moving in meters per second
     */
    private double speed;

    /**
     * The angular speed of the car. Positive to the left, negative to the right.
     */
    public double angularSpeed;

    private Matrix4f corners = new Matrix4f();

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
     * @return The length
     */
    public double getLength() {
        return length;
    }

    /**
     * Gets the Phyisical Entity's width
     * @return The width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Gets the Physical Entity's angle
     * @return The angle
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Gets the angle of the speed vector
     * @return The angle of the speed vector
     */
    public double getSpeedAngle() {return speedAngle;}

    /**
     * Sets the Physical Entity's angle
     * @param angle The angle to be set
     */
    public void setAngle(double angle) {
        this.angle = angle;
    }

    /**
     * Adds an angle to the Physical Entity's angle
     * @param angle The angle to be added
     */
    public void addAngle(double angle) {
        this.angle += angle;
    }

    @Override
    public List<List<Pair<Double,Double>>> getCornersOfAllHitBoxes() {
        corners.m00((float)(getX() + length));
        corners.m10((float)(getY() + (width / 2)));
        corners.m01((float)(getX() + length));
        corners.m11((float)(getY() - (width / 2)));
        corners.m02((float)(getX() - length));
        corners.m12((float)(getY() - (width / 2)));
        corners.m03((float)(getX() - length));
        corners.m13((float)(getY() + (width / 2)));

        corners.multiply(Matrix4f.rotationZ((float) angle, tempMatrix));

//        System.out.println("" + corners.m00() + " " + corners.m10());
//        System.out.println("" + corners.m01() + " " + corners.m11());
//        System.out.println("" + corners.m11() + " " + corners.m12());
//        System.out.println("" + corners.m03() + " " + corners.m13());
//        System.out.println("-------------------------------");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        glBegin(GL_QUADS);
//        glVertex2f(corners.m00(), corners.m01());
//        glVertex2f(corners.m01(), corners.m11());
//        glVertex2f(corners.m02(), corners.m12());
//        glVertex2f(corners.m03(), corners.m13());
//        glEnd();

        List<Pair<Double,Double>> corners = new ArrayList<>();
        List<List<Pair<Double,Double>>> hitboxes = new ArrayList<>();
        //TODO: Figure out how to put the corners here
//        corners.add();
        hitboxes.add(corners);
        return hitboxes;
    }

    @Override
    public void collided() {
        this.setSpeed(0);
    }

    /**
     * Gets this object's speed
     * @return The object's speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the object's speed
     * @param speed The speed
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Sets the object's angular speed
     * @param speed The speed
     */
    public void setAngularSpeed(double speed) {
        this.angularSpeed = speed;
    }

    /**
     * Gets the object's angular speed
     * @return The object's angular speed
     */
    public double getAngularSpeed() {
        return this.angularSpeed;
    }
}
