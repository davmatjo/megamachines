package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.util.Pair;

import java.util.List;

/**
 * All collidable objects must implement this interface
 */
public interface Collidable {
    /**
     * Returns a list of all hitboxes
     * @return The list of hitboxes
     */
    public List<List<Pair<Double,Double>>> getCornersOfAllHitBoxes();

    /**
     * Returns the body's velocity to the point of impact
     * The point of impact and the objects are always on the game world, so we assume
     * it's enough to get their velocity
     * First the speed, then the angle in degrees
     */
    public Pair<Double, Double> getVelocity();

    /**
     * 1 means perfectly elastic, 0 means plastic
     * You should probably return something close to 1 (> 0.7)
     * This gets multiplied by the other's object coefficient
     * @return The coefficient of restitution
     */
    public double getCoefficientOfRestitution();

    /**
     * Gets the mass of the object
     * @return The mass of the object
     */
    public double getMass();

    /**
     * Returns the vector from the object's center of mass to the collision point
     * @return The vector from the object's center of mass to the collision point
     */
    public double getVectorFromCenterOfMass(double xp, double yp);

    /**
     * Gets the object's rotational inertia
     * @return The object's rotational inertia
     */
    public double getRotationalInertia();

    /**
     * Gets the object's speed vector
     * @return The object's speed vector
     */
    public double getSpeedVector();

    /**
     * This function gets called when the object has collided
     */
    public default void collided(double xp, double yp, Collidable c2) {
        System.out.println("COLLISION YEEPEEE");

        Pair<Double, Double> firstVelocity = this.getVelocity();
        Pair<Double, Double> secondVelocity = c2.getVelocity();
        double firstX = firstVelocity.getFirst() * (Math.cos(Math.toRadians(firstVelocity.getSecond())));
        double secondX = secondVelocity.getFirst() * (Math.cos(Math.toRadians(secondVelocity.getSecond())));
        double firstY = firstVelocity.getFirst() * (Math.sin(Math.toRadians(firstVelocity.getSecond())));
        double secondY = secondVelocity.getFirst() * (Math.sin(Math.toRadians(secondVelocity.getSecond())));
        double x = firstX - secondX;
        double y = firstY - secondY;

        Pair<Double, Double> relativeVelocity = new Pair<Double, Double>
                                                    (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)),
                                                        Math.atan2(y, x));
    }
}
