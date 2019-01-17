package entities.abstractCarComponents;

import entities.EntityComponent;

/**
 * The abstract representation of car springs
 */
public abstract class Springs extends EntityComponent {
    /**
     * The amount of force a spring provides when it is compressed or extended to the maximum
     */
    private boolean maximumForceExerted;
}
