package com.battlezone.megamachines.world;

import com.battlezone.megamachines.math.Vector2f;

public class GameObject {

    private double x;
    private double y;
    private float scale;

    public GameObject(double x, double y, float scale) {
        this.x = x;
        this.y = y;
        this.scale = scale;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public float getXf() {
        return (float) x;
    }

    public float getYf() {
        return (float) y;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

}
