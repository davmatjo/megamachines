package com.battlezone.megamachines.util;

/**
 * A class that holds two values which are comparable.
 *
 * @param <F> The type of the first value.
 * @param <S> The type of the second value.
 */
public class ComparablePair<F extends Comparable, S extends Comparable> extends Pair implements Comparable<ComparablePair<F, S>> {

    public ComparablePair(F first, S second) {
        super(first, second);
    }

    @Override
    public int compareTo(ComparablePair<F, S> o) {
        // Greater than any null objects.
        if (o == null) return 1;
        // Check the first value
        if (this.first.equals(o.first)) {
            return ((S) second).compareTo((S) o.second);
        } else {
            // Check the second number
            return ((F) first).compareTo((F) o.first);
        }
    }
}
