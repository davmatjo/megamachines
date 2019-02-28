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
     * Gets the weight of the car body
     * @return The weight of the car body
     */
    public double getWeight() {
        return this.weight;
    }
}
