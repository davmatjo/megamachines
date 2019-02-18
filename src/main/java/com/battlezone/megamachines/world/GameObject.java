package com.battlezone.megamachines.world;

import com.battlezone.megamachines.renderer.game.animation.Animation;

/**
 * A game object.
 * Everything that moves or collides is a game object.
 */
public abstract class GameObject {

    /**
     * The x coordinate of this game object
     */
    private double x;
    /**
     * The y coordinate of this game object
     */
    private double y;
    /**
     * The scale of this game object
     */
    private float scale;

    /**
     * The speed at which this object is moving in meters per second
     */
    private double speed;

    /**
     * The constructor
     *
     * @param x     The x coordinate
     * @param y     The y coordinate
     * @param scale The scale
     */
    public GameObject(double x, double y, float scale) {
        this.x = x;
        this.y = y;
        this.scale = scale;
    }

    /**
     * Sets the x coordinate
     *
     * @param x The x coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Gets the x coordinate
     *
     * @return The x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the x coordinate as a float
     *
     * @return The x coordinate as a float
     */
    public float getXf() {
        return (float) getX();
    }


    /**
     * Sets the y coordinate
     *
     * @param y The y coordinate
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Gets the y coordinate
     *
     * @return The y coordinate
     */
    public double getY() {
        return y;
    }

    /***
     * Gets the y coordinate as a float
     * @return The y coordinate as a float
     */
    public float getYf() {
        return (float) getY();
    }

    /**
     * Gets the scale of this game object
     *
     * @return The scale of this game object
     */
    public float getScale() {
        return scale;
    }

    /**
     * Sets the scale of this game object
     *
     * @param scale The sclae of this game object
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Gets this object's speed
     *
     * @return The object's speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the object's speed
     *
     * @param speed
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void playAnimation(Animation anim) {
        anim.play(this);
    }
}
