package com.battlezone.megamachines.physics;

/**
 * This class contains all assumptions about the game world.
 * Note that some of the formulae are just simplifications of what would happen in real life.
 * 100% accurate com.battlezone.megamachines.physics are absolutely impossible for this kind of project.
 **/
public class WorldProperties {
    /**
     * An enumeration of possible track types
     */
    public enum RoadType {
        TRACK, DIRT, ICE
    }

    /**
     * An enumeration of possible environments
     */
    public enum Environment {
        EARTH, SPACE
    }
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

    /**
     * This world's road type
     */
    private RoadType roadType;

    /**
     * This world's environment
     */
    private Environment environment;

    /**
     * The constructor
     * @param roadType The road type this world should have
     * @param environment The environment this world should have
     */
    public WorldProperties(RoadType roadType, Environment environment) {

    }
}
