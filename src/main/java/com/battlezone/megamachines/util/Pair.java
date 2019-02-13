package com.battlezone.megamachines.util;

public class Pair<T, U> {

    T first;
    U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public U getSecond() {
        return second;
    }

    public void setSecond(U second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "(" + first.toString() + ", " + second.toString() + ")";
    }

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
        return (int) this.first.hashCode() + this.second.hashCode();
    }
}
