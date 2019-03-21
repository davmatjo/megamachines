package com.battlezone.megamachines.math;

import com.battlezone.megamachines.util.Pair;

/**
 * This class defines a few functions for 3D vectors.
 * The only place where we need such vectors is Collidable.
 */
public class Vector3d {
    /**
     * The length of this vector defined for each coordinate
     */
    public double x, y, z;

    /**
     * Constructs a Vector3D from a set of 3 coordinates
     *
     * @param x The x
     * @param y The y
     * @param z The z
     */
    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Constructs a Vector3D from a regular vector
     *
     * @param v The vector
     */
    public Vector3d(Pair<Double, Double> v) {
        this.x = v.getFirst() * Math.cos(v.getSecond());
        this.y = v.getFirst() * Math.sin(v.getSecond());
        this.z = 0;
    }

    /**
     * Returns the dot product of 2 vectors
     *
     * @param a The first vector
     * @param b The second vector
     * @return The cross product
     */
    public static double dotProduct(Vector3d a, Vector3d b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    /**
     * Returns the cross product of 2 vectors.
     *
     * @param a The first vector
     * @param b The second vector
     * @return The cross product of the 2 vectors
     */
    public static Vector3d crossProduct(Vector3d a, Vector3d b) {
        return new Vector3d(a.y * b.z - a.z * b.y, -a.x * b.z + a.z * b.x, a.x * b.y - a.y * b.x);
    }

    /**
     * Divides each coordinate by a set amount
     *
     * @param v The vector
     * @param c The amount to be divided by
     * @return The vector, with each coordinate divided
     */
    public static Vector3d divide(Vector3d v, double c) {
        return new Vector3d(v.x / c, v.y / c, v.z / c);
    }

    /**
     * Returns the length of the vector
     *
     * @param a The vector
     * @return The length of the vector
     */
    public static double getLength(Vector3d a) {
        return Math.sqrt(Math.pow(a.x, 2) + Math.pow(a.y, 2) + Math.pow(a.z, 2));
    }
}
