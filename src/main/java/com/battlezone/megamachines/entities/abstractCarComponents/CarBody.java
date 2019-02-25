package com.battlezone.megamachines.entities.abstractCarComponents;

import com.battlezone.megamachines.entities.EntityComponent;

/**
 * The abstract representation of a car body
 */
public abstract class CarBody extends EntityComponent {
    /**
     * The height at which the car body's center of weight is situated at.
     * As a simplification, this variable holds the center of weight of the car
     * And we ignore all of the other components.
     * (Which is useful because we don't have access to any real-life measurements)
     */
    private double centerOfWeightH;

    /**
     * The current amount of body roll along the length of the car
     * A positive number indicates an amount of weight transfered to the front wheels
     * A negative number indicates an amount of weight transfered to the rear wheels
     */
    private double bodyRollLength;

    /**
     * The current amount of body roll along the width of the car
     * A positive number indicates an amount of weight transfered to the right wheels
     * A negative number indicates an amount of weight transfered to the left wheels
     */
    private double bodyRollWidth;

    /**
     * Gets the weight of the car body
     * @return The weight of the car body
     */
    public double getWeight() {
        return this.weight;
    }
}
