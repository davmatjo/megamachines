package com.battlezone.megamachines.world;

import com.battlezone.megamachines.renderer.game.AbstractRenderable;
import com.battlezone.megamachines.renderer.game.Model;


public abstract class GameObject extends AbstractRenderable {

    private double x;
    private double y;
    private float scale;

    /**
     * The speed at which this object is moving in meters per second
     */
    private double speed;

    public GameObject(double x, double y, float scale, Model model) {
        super(model);
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
        return (float) getX();
    }

    public float getYf() {
        return (float) getY();
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Gets this object's speed
     * @return The object's speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the object's speed
     * @param speed
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
