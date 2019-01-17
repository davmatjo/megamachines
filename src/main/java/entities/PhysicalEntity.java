package entities;

import java.util.List;

/**
 * All the physical game entities should extend this class.
 * Here, we hold the basic properties of all phyisical entities.
 */
public abstract class PhysicalEntity {
    /**
     * The entity's X position in pixels
     */
    private double X;

    /**
     * The entity's Y position in pixels
     */
    private double Y;

    /**
     * The entity's length in pixels
     */
    private int length;

    /**
     * The entity's width in pixels
     */
    private int width;

    /**
     * The components that belong to the Phyisical Entity
     */
    private List<EntityComponent> components;

    /**
     * We are using the trigonometric interpretation of angles (with degrees, not radians)
     * An angle of 0 degrees means that the entity is pointing to the right.
     * An angle of 90 degrees means that the entity is pointing upwards.
     * An angle of -90 degrees means that the entity is pointing downwards.
     */
    private double angle;

    /**
     * Gets the Physical Entity's X position
     * @return The X position
     */
    public double getX() {
        return X;
    }

    /**
     * Sets the Physical Entity's X position
     * @param x The X position to be set
     */
    public void setX(double x) {
        X = x;
    }

    /**
     * Gets the Physical Entity's Y position
     * @return The Y position
     */
    public double getY() {
        return Y;
    }

    /**
     * Sets the Physical Entity's Y position
     * @param y The Y position to be set
     */
    public void setY(double y) {
        Y = y;
    }

    /**
     * Gets the Physical Entity's length
     * @return The length
     */
    public int getLength() {
        return length;
    }

    /**
     * Gets the Phyisical Entity's width
     * @return The width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the Physical Entity's angle
     * @return The angle
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Sets the Physical Entity's angle
     * @param angle The angle to be set
     */
    public void setAngle(double angle) {
        this.angle = angle;
    }

    /**
     * Adds an angle to the Physical Entity's angle
     * @param angle The angle to be added
     */
    public void addAngle(double angle) {
        this.angle += angle;
    }

    /**
     * Gets the Physical Entity's weight
     * @return The weight
     */
    public double getWeight() {
        int total = 0;
        for (int i = 0; i <components.size(); i++) {
            total += components.get(i).getWeight();
        }
        return total;
    }

    /**
     * Gets the Phyisical Entity's list of components
     * @return The list of components
     */
    public List<EntityComponent> getComponents() {
        return components;
    }

    /**
     * Adds a new component to the Phyisical Entity
     * @param c
     */
    public void addComponent(EntityComponent c) {
        components.add(c);
    }

    /**
     * Removes a component from the Physical Entity
     * @param c The component to be removed
     * @return True if the component was removed, false otherwise
     */
    public boolean removeComponent(Object c) {
        return components.remove(c);
    }
}
