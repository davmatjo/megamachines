package com.battlezone.megamachines.physics;

/**
 * This class contains all assumptions about the game world.
 * Note that some of the formulae are just simplifications of what would happen in real life.
 * 100% accurate com.battlezone.megamachines.physics are absolutely impossible for this kind of project.
 **/
public class WorldProperties {
    /**
     * Gravitational pull
     */
    public static double g = 9.81;

    /**
     * The maximum tyre traction on normal road is proportional to the friction with the road.
     **/
    public static double tyreFrictionRoadMultiplier = 1;

    /**
     * The maximum tyre traction on dirt is proportional to the friction with the road.
     */
    public static double tyreFrictionDirtMultiplier = 0.5;

    /**
     * The maximum tyre traction on ice is proportional to the friction with the road.
     */
    public static double tyreFrictionIceMultiplier = 0.1;
}
