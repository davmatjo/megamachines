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
    public default Pair<Double, Double> getVectorFromCenterOfMass(double xp, double yp, Pair<Double, Double> position) {
        double x = position.getFirst();
        double y = position.getSecond();

        double dx = xp - x;
        double dy = yp - y;

        return new Pair<Double, Double>(Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)),
                Math.atan2(dy, dx));
    }

    /**
     * Returns this object's center of mass (x, y) position
     * @return The object's center of mass (x, y) position
     */
    public Pair<Double, Double> getCenterOfMassPosition();

    /**
     * Gets the object's rotational inertia
     * @return The object's rotational inertia
     */
    public double getRotationalInertia();

    /**
     * Returns the dot product of 2 vectors
     * @param a The first vector
     * @param b The second vector
     * @return The cross product
     */
    public static double dotProduct(Pair<Double, Double> a, Pair<Double, Double> b) {
        return a.getFirst() * b.getFirst() * Math.cos((b.getSecond() - a.getSecond()));
    }

    /**
     * Returns the cross product of 2 vectors. Please mind that the angle points on a vertical direction, i.e. not on the plane
     * @param a The first vector
     * @param b The second vector
     * @return The cross product of the 2 vectors
     */
    public static Pair<Double, Double> crossProduct(Pair<Double, Double> a, Pair<Double, Double> b) {
        return new Pair<Double, Double>(a.getFirst() * b.getFirst() * Math.sin(b.getSecond() - a.getSecond()), 0.0);
    }

    public static Pair<Double, Double> divide(Pair<Double, Double> a, double c){
        return new Pair<Double, Double>(a.getFirst() / c, a.getSecond());
    }

    /**
     * Tells the collidable object to add a vector to the object's speed vector
     * @param impactResult The resulting vector from the impact
     */
    public void applyVelocityDelta(Pair<Double,Double> impactResult);

    /**
     * Applies an angular velocity to the object
     * @param delta The delta to be applied
     */
    public void applyAngularVelocityDelta(double delta);


    /**
     * This function gets called when the object has collided
     */
    public default void collided(double xp, double yp, Collidable c2) {
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

        Pair<Double, Double> unitVector = new Pair<Double,Double>(1.0, relativeVelocity.getSecond());

        Pair<Double, Double> vector1FromCenterOfMass = getVectorFromCenterOfMass(xp, yp, this.getCenterOfMassPosition());
        Pair<Double, Double> vector2FromCenterOfMass = c2.getVectorFromCenterOfMass(xp, yp, c2.getCenterOfMassPosition());

        double restitution = getCoefficientOfRestitution() * c2.getCoefficientOfRestitution();

        double energy;

        double angularEffects1, angularEffects2;

        Pair<Double, Double> angularEffects;
        Pair<Double, Double> temp;

        temp = new Pair<>(dotProduct(unitVector, divide(crossProduct(vector1FromCenterOfMass, unitVector), getRotationalInertia())), unitVector.getSecond());
        angularEffects = crossProduct(temp, vector1FromCenterOfMass);
        angularEffects1 = Math.cos(angularEffects.getSecond());

        temp = new Pair<>(dotProduct(unitVector, divide(crossProduct(vector2FromCenterOfMass, unitVector), c2.getRotationalInertia())), unitVector.getSecond());
        angularEffects = crossProduct(temp, vector2FromCenterOfMass);
        angularEffects2 = Math.cos(angularEffects.getSecond());

        energy = -((relativeVelocity.getFirst() * (restitution + 1)) /
                ((1 / getMass()) + (1 / c2.getMass()) + angularEffects1 + angularEffects2));

        //TODO: Modify this to control the amount of energy involved in the collision
        energy *= 100;


        applyVelocityDelta(new Pair<>(unitVector.getFirst() * energy / getMass(), Math.toDegrees(unitVector.getSecond())));
        c2.applyVelocityDelta(new Pair<>(-unitVector.getFirst() * energy / c2.getMass(), Math.toDegrees(unitVector.getSecond())));

        temp = crossProduct(vector1FromCenterOfMass, new Pair<>(unitVector.getFirst() * energy, unitVector.getSecond()));
        temp.setFirst(temp.getFirst() * Math.cos(temp.getSecond()));

        applyAngularVelocityDelta(temp.getFirst() / getRotationalInertia());

        temp = crossProduct(vector2FromCenterOfMass, new Pair<>(-unitVector.getFirst() * energy, unitVector.getSecond()));
        temp.setFirst(temp.getFirst() * Math.cos(temp.getSecond()));

        c2.applyAngularVelocityDelta(temp.getFirst() / c2.getRotationalInertia());
    }
}
