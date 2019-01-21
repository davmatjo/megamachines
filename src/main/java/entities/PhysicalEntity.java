package entities;

import com.battlezone.megamachines.math.Vector2f;
import com.battlezone.megamachines.world.GameObject;

import java.util.List;

/**
 * All the physical game entities should extend this class.
 * Here, we hold the basic properties of all phyisical entities.
 */
public abstract class PhysicalEntity extends GameObject {
    /**
     * The entity's length in meters
     */
    private double length;

    /**
     * The entity's width in meters
     */
    private double width;

    /**
     * The speed at which this object is moving in meters per second
     */
    private double speed;

    /**
     * We are using the trigonometric interpretation of angles (with degrees, not radians)
     * An angle of 0 degrees means that the entity is pointing to the right.
     * An angle of 90 degrees means that the entity is pointing upwards.
     * An angle of -90 degrees means that the entity is pointing downwards.
     */
    private double angle;

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

    /**
     * Gets the Physical Entity's weight
     * @return The weight
     */
//    public double getWeight() {
//        int total = 0;
//        for (int i = 0; i <components.size(); i++) {
//            total += components.get(i).getWeight();
//        }
//        return total;
//    }

    /**
     * Gets this object's speed
     * @return The object's speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the object's speed
     * @param speed
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
