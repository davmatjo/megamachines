package entities.abstractCarComponents;

import entities.EntityComponent;

/**
 * The abstract representation of a car wheel
 */
public abstract class Wheel extends EntityComponent {
    /**
     * The speed at which the car would move if the wheel would not be slipping
     */
    private double wheelSpeed;
}
