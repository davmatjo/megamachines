package com.battlezone.megamachines.util;

import java.io.Serializable;

public class Pair<T, U> implements Serializable {

    private T first;
    private U second;

    /**
     * Creates a pair of the given values.
     *
     * @param first  The first value.
     * @param second The second value.
     */
    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Gets the first value.
     *
     * @return The first value.
     */
    public T getFirst() {
        return first;
    }

    /**
     * Sets the first value.
     *
     * @param first The new value of the first value.
     */
    public void setFirst(T first) {
        this.first = first;
    }

    /**
     * Gets the second value.
     *
     * @return The second value.
     */
    public U getSecond() {
        return second;
    }

    /**
     * Sets the second value.
     *
     * @param second The new value of the second value.
     */
    public void setSecond(U second) {
        this.second = second;
    }

    /**
     * Sets the new first and second values.
     *
     * @param first  The new value of the first value.
     * @param second The new value of the second value.
     */
    public void set(T first, U second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "(" + first.toString() + ", " + second.toString() + ")";
    }

    /**
     * A method to determine whether a pair is equal to another object.
     *
     * @param f The object to check for equality.
     * @return Whether the two objects are equal.
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object f) {
        if (f instanceof Pair) {
            return (this.getFirst().equals(((Pair) f).getFirst()) && this.getSecond().equals(((Pair) f).getSecond()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.first.hashCode() + this.second.hashCode();
    }
}
