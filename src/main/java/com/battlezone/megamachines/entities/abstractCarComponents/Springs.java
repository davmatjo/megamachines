package com.battlezone.megamachines.entities.abstractCarComponents;

import com.battlezone.megamachines.entities.EntityComponent;

/**
 * The abstract representation of car springs
 */
public abstract class Springs extends EntityComponent {
    /**
     * The amount of longitudinalForce a spring provides when it is compressed or extended to the maximum
     */
    private boolean maximumForceExerted;
}
