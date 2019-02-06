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

        return new Pair<Double, Double>(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)),
                Math.atan2(y, x));
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
        return a.getFirst() * b.getFirst() * Math.cos(Math.toRadians(b.getSecond() - a.getSecond()));
    }

    /**
     * Returns the cross product of 2 vectors. Please mind that the angle points on a vertical direction, i.e. not on the plane
     * @param a The first vector
     * @param b The second vector
     * @return The cross product of the 2 vectors
     */
    public static Pair<Double, Double> crossProduct(Pair<Double, Double> a, Pair<Double, Double> b) {
        return new Pair<Double, Double>(a.getFirst() * b.getFirst() * Math.sin(Math.toRadians(b.getSecond() - a.getSecond())), 0.0);
    }

    public static Pair<Double, Double> divide(Pair<Double, Double> a, double c){
        return new Pair<Double, Double>(a.getFirst() / c, a.getSecond());
    }

    public void applyVelocityDelta(Pair<Double,Double> impactResult);

    public void applyAngularVelocityDelta(double delta);


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

        Pair<Double, Double> unitVector = new Pair<Double,Double>(1.0, relativeVelocity.getSecond());

        Pair<Double, Double> vector1FromCenterOfMass = getVectorFromCenterOfMass(xp, yp, this.getCenterOfMassPosition());
        Pair<Double, Double> vector2FromCenterOfMass = c2.getVectorFromCenterOfMass(xp, yp, this.getCenterOfMassPosition());

        double restitution = getCoefficientOfRestitution() * c2.getCoefficientOfRestitution();

        double energy;

        double angularEffects1, angularEffects2;

        Pair<Double, Double> angularEffects;
        Pair<Double, Double> temp;

        temp = new Pair<>(dotProduct(unitVector, divide(crossProduct(vector1FromCenterOfMass, unitVector), getRotationalInertia())), unitVector.getSecond());

        angularEffects = crossProduct(temp, vector1FromCenterOfMass);

        if (angularEffects.getSecond() == 0) {
            angularEffects1 = -angularEffects.getFirst();
        } else if (angularEffects.getSecond() % 180 == 0) {
            angularEffects1 = angularEffects.getFirst();
        } else {
            System.out.println("Something really bad something in collisions");
            angularEffects1 = 0;
        }

        temp = new Pair<>(dotProduct(unitVector, divide(crossProduct(vector2FromCenterOfMass, unitVector), c2.getRotationalInertia())), unitVector.getSecond());

        angularEffects = crossProduct(temp, vector2FromCenterOfMass);

        if (angularEffects.getSecond() == 0) {
            angularEffects2 = -angularEffects.getFirst();
        } else if (angularEffects.getSecond() % 180 == 0) {
            angularEffects2 = angularEffects.getFirst();
        } else {
            System.out.println("Something really bad something in collisions");
            angularEffects2 = 0;
        }

        energy = -((relativeVelocity.getFirst() * (restitution + 1)) /
                ((1 / getMass()) + (1 / c2.getMass()) + angularEffects1 + angularEffects2));


        applyVelocityDelta(new Pair<>(unitVector.getFirst() * energy / getMass(), unitVector.getSecond()));
        applyVelocityDelta(new Pair<>(-unitVector.getFirst() * energy / c2.getMass(), unitVector.getSecond()));

        temp = crossProduct(vector1FromCenterOfMass, new Pair<>(unitVector.getFirst() * energy, unitVector.getSecond()));
        if (temp.getSecond() != 0) {
            temp.setFirst(-temp.getFirst());
        }

        applyAngularVelocityDelta(temp.getFirst() / getRotationalInertia());

        temp = crossProduct(vector2FromCenterOfMass, new Pair<>(unitVector.getFirst() * energy, unitVector.getSecond()));
        if (temp.getSecond() != 0) {
            temp.setFirst(-temp.getFirst());
        }

        applyAngularVelocityDelta(temp.getFirst() / c2.getRotationalInertia());
    }
}
