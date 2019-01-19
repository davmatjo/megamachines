package entities;

/**
 * All entity components should extend this class. (e.g. a car's engine)
 * Entity components don't inhabit the phyisical game world. They are part of some Physical Entity.
 */
public abstract class EntityComponent {
    /**
     * The offset on the X axis from the original Entity's position
     */
    private double XOffset;

    /**
     * The offset on the Y axis fromthe original Entity's position
     */
    private double YOffset;

    /**
     * The weight measured in kilograms
     */
    protected double weight = 0;

    /**
     * Gets the Entity Component's X Offset
     * @return The X Offset
     */
    public double getXOffset() {
        return XOffset;
    }

    /**
     * Gets the Entity Component's Y Offset
     * @return The Y offset
     */
    public double getYOffset() {
        return YOffset;
    }

    /**
     * Gets the Entity Component's weight
     * @return The weight
     */
    public double getWeight() {
        return weight;
    }
}
