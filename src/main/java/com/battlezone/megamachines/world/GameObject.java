package com.battlezone.megamachines.world;

import com.battlezone.megamachines.math.Vector2f;
import com.battlezone.megamachines.renderer.game.AbstractRenderable;
import com.battlezone.megamachines.renderer.game.Model;
import com.battlezone.megamachines.renderer.game.Shader;

public abstract class GameObject extends AbstractRenderable {

    private double x;
    private double y;
    private float scale;

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

    public double getXInMeters() {
        return x;
    }

    public double getYInMeters() {
        return y;
    }

    public double getXInPixels() {
        return x;
    }

    public double getYInPixels() {
        return y;
    }

    public float getXf() {
        return (float) getXInPixels();
    }

    public float getYf() {
        return (float) getYInPixels();
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
