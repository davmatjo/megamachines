package com.battlezone.megamachines.entities;

/**
 * This interface defines an object which can use powerups
 */
public interface PowerupUser {
    /**
     * This function gets called when an agility powerup has been activated for this car
     */
    void agilityActivated();

    /**
     * This function gets called when an agility powerup has been deactivated for this car
     */
    void agilityDeactivated();

    /**
     * This function gets called an a growth powerup has been activated for this car
     */
    void growthActivated();

    /**
     * This function gets called when a growth powerup has been deactivated for this car
     */
    void growthDeactivated();
}
