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

        return new Pair<Double, Double>(2 * Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)),
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
     * Corrects collision based on velocity difference vector
     * @param velocityDifference The velocity difference vector
     */
    public void correctCollision(Pair<Double, Double> velocityDifference);


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
        Vector3D relativeVelocity3D = new Vector3D(relativeVelocity);

        this.correctCollision(relativeVelocity);
        c2.correctCollision(relativeVelocity);

        Pair<Double, Double> unitVector = new Pair<Double,Double>(1.0, relativeVelocity.getSecond());
        Vector3D unitVector3D = new Vector3D(unitVector);

        Pair<Double, Double> vector1FromCenterOfMass = getVectorFromCenterOfMass(xp, yp, this.getCenterOfMassPosition());
        Pair<Double, Double> vector2FromCenterOfMass = c2.getVectorFromCenterOfMass(xp, yp, c2.getCenterOfMassPosition());
        Vector3D vector1FromCenterOfMass3D = new Vector3D(vector1FromCenterOfMass);
        Vector3D vector2FromCenterOfMass3D = new Vector3D(vector2FromCenterOfMass);

        double restitution = getCoefficientOfRestitution() * c2.getCoefficientOfRestitution();

        double energy;

        double angularEffects1, angularEffects2;

        angularEffects1 = Vector3D.dotProduct(unitVector3D, Vector3D.crossProduct(Vector3D.divide(Vector3D.crossProduct(vector1FromCenterOfMass3D, unitVector3D), getRotationalInertia()), vector1FromCenterOfMass3D));
        angularEffects2 = Vector3D.dotProduct(unitVector3D, Vector3D.crossProduct(Vector3D.divide(Vector3D.crossProduct(vector2FromCenterOfMass3D, unitVector3D), c2.getRotationalInertia()), vector2FromCenterOfMass3D));

        energy = -((relativeVelocity.getFirst() * (restitution + 1)) /
                ((1 / getMass()) + (1 / c2.getMass()) + angularEffects1 + angularEffects2));

        applyVelocityDelta(new Pair<>(energy / getMass(), Math.toDegrees(unitVector.getSecond())));
        c2.applyVelocityDelta(new Pair<>(-energy / c2.getMass(), Math.toDegrees(unitVector.getSecond())));

        unitVector3D = Vector3D.divide(unitVector3D, 1.0/energy);

        applyAngularVelocityDelta(Vector3D.getLenght(Vector3D.crossProduct(vector1FromCenterOfMass3D, unitVector3D)) / getRotationalInertia());
        c2.applyAngularVelocityDelta(-Vector3D.getLenght(Vector3D.crossProduct(vector2FromCenterOfMass3D, unitVector3D)) / c2.getRotationalInertia());
    }

    /**
     * This class defines a few functions for 3D vectors.
     * This is the only place where we need such vectors
     */
    class Vector3D {
        /**
         * The length of this vector defined for each coordinate
         */
        public double x,y,z;

        /**
         * Constructs a Vector3D from a set of 3 coordinates
         * @param x The x
         * @param y The y
         * @param z The z
         */
        public Vector3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        /**
         * Constructs a Vector3D from a regular vector
         * @param v The vector
         */
        public Vector3D(Pair<Double, Double> v) {
            this.x = v.getFirst() * Math.cos(v.getSecond());
            this.y = v.getFirst() * Math.sin(v.getSecond());
            this.z = 0;
        }

        /**
         * Returns the dot product of 2 vectors
         * @param a The first vector
         * @param b The second vector
         * @return The cross product
         */
        public static double dotProduct(Vector3D a, Vector3D b) {
            return a.x * b.x + a.y * b.y + a.z * b.z;
        }

        /**
         * Returns the cross product of 2 vectors.
         * @param a The first vector
         * @param b The second vector
         * @return The cross product of the 2 vectors
         */
        public static Vector3D crossProduct(Vector3D a, Vector3D b) {
            return new Vector3D(a.y * b.z - a.z * b.y, -a.x * b.z + a.z * b.x, a.x * b.y - a.y * b.x);
        }

        /**
         * Divides each coordinate by a set amount
         * @param v The vector
         * @param c The amount to be divided by
         * @return The vector, with each coordinate divided
         */
        public static Vector3D divide(Vector3D v, double c){
            return new Vector3D(v.x / c, v.y / c, v.z / c);
        }

        /**
         * Returns the length of the vector
         * @param a The vector
         * @return The length of the vector
         */
        public static double getLenght(Vector3D a) {
            return Math.sqrt(Math.pow(a.x, 2) + Math.pow(a.y, 2) + Math.pow(a.z, 2));
        }
    }
}
