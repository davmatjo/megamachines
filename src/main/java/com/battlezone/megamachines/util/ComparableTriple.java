package com.battlezone.megamachines.util;

/**
 * A class that holds 3 comparable types, with the order of precendence being first, second, third.
 *
 * @param <F> The type of the first comparable item to hold.
 * @param <S> The type of the second comparable item to hold.
 * @param <T> The type of the third comparable item to hold.
 */
public class ComparableTriple<F extends Comparable, S extends Comparable, T extends Comparable> implements Comparable<ComparableTriple<F, S, T>> {

    private F first;
    private S second;
    private T third;

    /**
     * Creates a comparable triple from the given values.
     *
     * @param first  The first value.
     * @param second The second value.
     * @param third  The third value.
     */
    public ComparableTriple(F first, S second, T third) {
        assert !(first == null || second == null || third == null);
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
     * @param first The new value for the first value.
     */
    public void setFirst(F first) {
        assert first != null;
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
     * @param second The new value for the second value.
     */
    public void setSecond(S second) {
        assert second != null;
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
     * @param third The new value for the third value.
     */
    public void setThird(T third) {
        assert third != null;
        this.third = third;
    }

    /**
     * The method that compares a triple to another triple.
     *
     * @param o The triple to compare to.
     * @return The value from the comparisons of the values.
     * @see Comparable#compareTo(Object)
     */
    @Override
    public int compareTo(ComparableTriple<F, S, T> o) {
        assert o != null;

        if (first == o.first || first.compareTo(o.first) == 0) {
            if (second == o.second || second.compareTo(o.second) == 0) {
                return third.compareTo(o.third);
            } else {
                return second.compareTo(o.second);
            }
        } else {
            return first.compareTo(o.first);
        }

    }

    /**
     * A method to set all three values at once.
     *
     * @param first  The new value for the first value.
     * @param second The new value for the second value.
     * @param third  The new value for the third value.
     */
    public void set(F first, S second, T third) {
        assert !(first == null || second == null || third == null);
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
