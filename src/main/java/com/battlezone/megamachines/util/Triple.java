package com.battlezone.megamachines.util;

public class Triple<F, S, T> {

    private F first;
    private S second;
    private T third;

    /**
     * Creates a triple from the three given values.
     *
     * @param first  The first value.
     * @param second The second value.
     * @param third  The third value.
     */
    public Triple(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * Gets the first value.
     *
     * @return The first value.
     */
    public F getFirst() {
        return first;
    }

    /**
     * Sets the first value.
     *
     * @param first The new value of the first value.
     */
    public void setFirst(F first) {
        this.first = first;
    }

    /**
     * Gets the second value.
     *
     * @return The second value.
     */
    public S getSecond() {
        return second;
    }

    /**
     * Sets the second value.
     *
     * @param second The new value of the second value.
     */
    public void setSecond(S second) {
        this.second = second;
    }

    /**
     * Gets the third value.
     *
     * @return The third value.
     */
    public T getThird() {
        return third;
    }

    /**
     * Sets the third value.
     *
     * @param third The new value of the third value.
     */
    public void setThird(T third) {
        this.third = third;
    }

    /**
     * Sets the three values.
     *
     * @param first  The new value of the first value.
     * @param second The new value of the second value.
     * @param third  The new value of the third value.
     */
    public void set(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
