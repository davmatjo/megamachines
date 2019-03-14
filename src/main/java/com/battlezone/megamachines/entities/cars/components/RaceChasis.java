package com.battlezone.megamachines.entities.cars.components;

import com.battlezone.megamachines.entities.cars.components.abstracted.CarBody;

/**
 * A race car chasis. This is lighter than a regular car's chasis
 */
public class RaceChasis extends CarBody {
    /**
     * The constructor
     */
    public RaceChasis() {
        this.weight = 800;
    }
}
