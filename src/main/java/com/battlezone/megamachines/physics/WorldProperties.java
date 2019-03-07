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
        TRACK, DIRT, ICE, MAGNETIC
    }

    /**
     * An enumeration of possible environments
     */
    public enum Environment {
        EARTH, SPACE
    }
    /**
     * This world's road type
     */
    private RoadType roadType;

    /**
     * This world's environment
     */
    private Environment environment;

    /**
     * This world's tyre friction multiplier
     * The bigger this number is, the more cars stick to the track
     */
    public double tyreFrictionMultiplier;

    /**
     * This world's gravitational constant
     */
    public double g;

    /**
     * The constructor
     * @param roadType The road type this world should have
     * @param environment The environment this world should have
     */
    public WorldProperties(RoadType roadType, Environment environment) {
        if (roadType == RoadType.TRACK) {
            tyreFrictionMultiplier = 1;
        } else if (roadType == RoadType.DIRT) {
            tyreFrictionMultiplier = 0.5;
        } else if (roadType == RoadType.ICE) {
            tyreFrictionMultiplier = 0.1;
        } else if (roadType == RoadType.MAGNETIC) {
            tyreFrictionMultiplier = 50;
        }

        if (environment == Environment.SPACE) {
            g = 0.1;
        } else {
            g = 9.81;
        }
    }
}
