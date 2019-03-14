package com.battlezone.megamachines.util;

public class ComparableTriple<F extends Comparable, S extends Comparable, T extends Comparable> implements Comparable<ComparableTriple<F, S, T>> {

    private F first;
    private S second;
    private T third;

    public ComparableTriple(F first, S second, T third) {
        assert !(first == null || second == null || third == null);
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public T getThird() {
        return third;
    }

    public void setFirst(F first) {
        assert first != null;
        this.first = first;
    }

    public void setSecond(S second) {
        assert second != null;
        this.second = second;
    }

    public void setThird(T third) {
        assert third != null;
        this.third = third;
    }

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

    public void set(F first, S second, T third) {
        assert !(first == null || second == null || third == null);
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
