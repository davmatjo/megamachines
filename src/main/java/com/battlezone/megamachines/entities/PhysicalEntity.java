package com.battlezone.megamachines.entities;

import com.battlezone.megamachines.renderer.game.Model;
import com.battlezone.megamachines.renderer.game.Shader;
import com.battlezone.megamachines.world.GameObject;

/**
 * All the physical game com.battlezone.megamachines.entities should extend this class.
 * Here, we hold the basic properties of all phyisical com.battlezone.megamachines.entities.
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
     * We are using the trigonometric interpretation of angles (with degrees, not radians)
     * An angle of 0 degrees means that the entity is pointing to the right.
     * An angle of 90 degrees means that the entity is pointing upwards.
     * An angle of -90 degrees means that the entity is pointing downwards.
     */
    private double angle = 0.0;

    public PhysicalEntity(double x, double y, float scale, Model model) {
        super(x, y, scale, model);
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
}
