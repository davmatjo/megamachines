package physics;

/**
 * This class contains all assumptions about the game world.
 * Note that some of the formulae are just simplifications of what would happen in real life.
 * 100% accurate physics are absolutely impossible for this kind of project.
 **/
public static class WorldProperties {
    /**
     * One meter is defined as a distance equal to 100 pixels.
     **/
    static double meter = 100;

    /**
     * Gravitational pull
     */
    public static double g = 9.81;

    /**
     * The maximum tyre traction on normal road is proportional to the friction with the road.
     **/
    public static double tyreFrictionRoad = 1.5;

    /**
     * The maximum tyre traction on dirt is proportional to the friction with the road.
     */
    public static double tyreFrictionDirt = 0.5;

    /**
     * The maximum tyre traction on ice is proportional to the friction with the road.
     */
    public static double tyreFrictionIce = 0.1;
}
