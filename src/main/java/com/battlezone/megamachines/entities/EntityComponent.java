package com.battlezone.megamachines.entities;

/**
 * All entity components should extend this class. (e.g. a car's engine)
 * Entity components don't inhabit the phyisical game world. They are part of some Physical Entity.
 */
public abstract class EntityComponent {
    /**
     * The weight measured in kilograms
     */
    protected double weight = 0;

    /**
     * Gets the Entity Component's weight
     *
     * @return The weight
     */
    public double getWeight() {
        return weight;
    }
}
